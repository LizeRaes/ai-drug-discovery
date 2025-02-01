package ma.devoxx.langchain4j.domain;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.UserMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.aiservices.*;
import ma.devoxx.langchain4j.molecules.Antibody;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;


@ApplicationScoped
public class StateMachine {

    private static final Logger logger = LoggerFactory.getLogger(StateMachine.class);

    @Inject
    CustomResearchState customResearchState;

    @Inject
    DiseasePicker diseasePicker;

    @Inject
    CustomResearchProject customResearchProject;

    @Inject
    AntigenFinder antigenFinder;

    @Inject
    KnownAntibodyFinder knownAntibodyFinder;

    @Inject
    KnownAntibodyCharacteristicsFinder knownAntibodyCharacteristicsFinderFinder;

    @Inject
    CdrFinder cdrFinder;

    @Inject
    NewAntibodyFinder newAntibodyFinder;

    @Inject
    CharacteristicsMeasurer measureCharacteristics;

    @Inject
    ArticlePublisher articlePublisher;

    @Inject
    StateManager stateManager;

    List<ChatMessage> messages;

    public void init() {
        customResearchState.getResearchState().moveToStep(ResearchState.Step.DEFINE_DISEASE);
    }

    public void loadState(Consumer<Message> messageConsumer) {
        messages = stateManager.loadChatMessage();
        if (messages.isEmpty()) {
            customResearchState.getResearchState().moveToStep(ResearchState.Step.DEFINE_DISEASE);
        } else {
            messages.forEach(message -> {
                if (message.type().equals(ChatMessageType.USER)) {
                    messageConsumer.accept(Message.userMessage(((UserMessage) message).singleText()));
                } else if (message.type().equals(ChatMessageType.AI)) {
                    messageConsumer.accept(Message.aiMessage(((AiMessage) message).text()));
                }
            });
        }
    }

    public void run(Integer userId, String userMessage, Consumer<Message> messageConsumer) {
        try {
            // ************************** STEP 1 **************************
            if (customResearchState.getResearchState().currentStep == ResearchState.Step.DEFINE_DISEASE) {
                logger.info("IN STEP 1 (define target disease)");
                String answer = diseasePicker.answer(userId, userMessage);
//                logger.info("*** Model Answer ***: " + answer);
                if (customResearchState.getResearchState().currentStep == ResearchState.Step.DEFINE_DISEASE) {
                    // disease was not finally decided on yet
                    messageConsumer.accept(Message.aiMessage(answer));
                    return;
                }
            }

            if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_ANTIGEN) {
                // else: model has set diseaseName and currentStep = 2 when decided on disease
                logger.info("******************** STEP 2 *********************");
                messageConsumer.accept(Message.aiMessage("Finding antigen info for " + customResearchProject.getResearchProject().disease + "..."));

                String answer = antigenFinder.determineAntigenInfo(customResearchProject.getResearchProject().disease);

                // if something went wrong with the antigenFinder
                if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_ANTIGEN) {
                    String notification = "UNEXPECTED STEP 2: failed at finding antigen name or sequence. Current state: " + customResearchState.getResearchState().currentStep;
                    logger.info(notification);
                    messageConsumer.accept(Message.aiMessage(notification));
                    return;
                }

                String result = "**I found antigen:** " + customResearchProject.getResearchProject().antigenName + "\n\n"
                        + "**With sequence:**\n" + customResearchProject.getResearchProject().antigenSequence;
                messageConsumer.accept(Message.aiMessage(result.replace("\n", "\n\n")));
            }

            if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_KNOWN_ANTIBODIES) {
                logger.info("******************** STEP 3 *********************");
                messageConsumer.accept(Message.aiMessage("I'm searching the literature to find known antibodies for " + customResearchProject.getResearchProject().antigenName + "..."));
                String answer = knownAntibodyFinder.getAntibodies(userId, customResearchProject.getResearchProject().antigenName, customResearchProject.getResearchProject().disease);
                // if we didn't move to step 4, no antibodies were found
                if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_KNOWN_ANTIBODIES) {
                    // TODO give it a retry
                    messageConsumer.accept(Message.aiMessage(answer));
                    messageConsumer.accept(Message.aiMessage("ERROR: no antibodies were found for this antigen. Please reload the page and start over."));
                    return;
                }

                knownAntibodyCharacteristicsFinderFinder.getAntibodyCharacteristics(userId, customResearchProject.getResearchProject().antigenName, customResearchProject.getResearchProject().disease, customResearchProject.getResearchProject().existingAntibodies.toString());
                // we ask for the user's input at this point
                // TODO make layout better (in line with what comes out of LLM)
                messageConsumer.accept(Message.aiMessage("We found the following antibodies:\n\n"
                        + customResearchProject.getResearchProject().printAntibodies() +
                        "\n\nWhich antibodies would you like to proceed with?"));
                return;
            }

            if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_KNOWN_CDRS) {
                logger.info("******************** STEP 4 *********************");
                String answer = cdrFinder.getCdrs(userId, userMessage);

                messageConsumer.accept(Message.aiMessage(answer));

                if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_KNOWN_CDRS) {
                    // still deciding on which antibodies to proceed with
                    return;
                }
            }

            if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_NEW_ANTIBODIES) {
                logger.info("******************** STEP 5 *********************");
                messageConsumer.accept(Message.aiMessage("Designing new antibodies based on known antibodies..."));
                // we ask for confirmation here bcs it's costly calculations / experiments
                String answer = newAntibodyFinder.getAntibodies(customResearchProject.getResearchProject().printAntigenInfo(), customResearchProject.getResearchProject().printExistingAntibodiesWithCDR());
                messageConsumer.accept(Message.aiMessage(answer));
                return;
            }

            if (customResearchState.getResearchState().currentStep == ResearchState.Step.MEASURE_CHARACTERISTICS) {
                logger.info("******************** STEP 6 *********************");
                for (Antibody antibody : customResearchProject.getResearchProject().getNewAntibodiesWithCdrs()) {
                    messageConsumer.accept(Message.aiMessage("Measuring characteristics for " + antibody.antibodyName + "..."));
                    messageConsumer.accept(Message.aiMessage(measureCharacteristics.measureCharacteristics(antibody.antibodyName, antibody.cdrs, customResearchProject.getResearchProject().antigenSequence, customResearchProject.getResearchProject().antigenName)));
                }
                messageConsumer.accept(Message.aiMessage("All characteristics were measured. Do you want to publish the results of this research in Nature or in the New York Times?"));
                return;
            }

            if (customResearchState.getResearchState().currentStep == ResearchState.Step.FINISHED) {
                logger.info("calling AiService ArticlePublisher with ResearchProject: " + customResearchProject.getResearchProject().toString() + "and authors: Mohamed Software and Lize Dev");
                // give this AiService a clean memory
                String answer = articlePublisher.publishArticle(userId + 1, userMessage, customResearchProject.getResearchProject().toString(), "Mohamed Software and Lize Dev");
                messageConsumer.accept(Message.aiMessage(answer));
                return;
            }

            messageConsumer.accept(Message.aiMessage("We shouldn't get in this state. But thank you for using Ai Drug Discovery Researcher!"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn(e.getMessage());
        }
    }
}
