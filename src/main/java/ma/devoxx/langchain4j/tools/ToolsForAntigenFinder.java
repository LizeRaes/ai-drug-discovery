package ma.devoxx.langchain4j.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.AugmentationRequest;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.aiservices.Chat;
import ma.devoxx.langchain4j.dbs.PublicProteinDatabase;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentor;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentorV2;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchState;

import java.io.Serializable;
import java.util.logging.Logger;

@ApplicationScoped
public class ToolsForAntigenFinder implements Serializable {
    CustomResearchProject customResearchProject;
    CustomResearchState customResearchState;

    @Inject
    CustomRetrievalAugmentorV2 customRetrievalAugmentor;

    @Inject
    ChatLanguageModel model;


    public ToolsForAntigenFinder(CustomResearchProject customResearchProject, CustomResearchState customResearchState) {
        this.customResearchProject = customResearchProject;
        this.customResearchState = customResearchState;
    }

    @Tool("tool to find disease and sequence information over different available scientific sources")
    public String findDiseaseAndSequenceInfo(@P("comprehensive query of the information you are tyring to find") String query) {
        System.out.println("findDiseaseAndSequenceInfo() called with query='" + query + "'");
        Chat chatbot = AiServices.builder(Chat.class)
                .chatLanguageModel(model)
                .retrievalAugmentor(customRetrievalAugmentor.getRetrievalAugmentor())
                .build();
        return chatbot.answer(query);
    }

    @Tool("store antigen name and antigen sequence")
    public void storeAntigenInfo(String antigenName, @P("Light Chain and Heavy Chain only, markdown format")String antigenSequence) {
        Logger.getLogger(ToolsForAntigenFinder.class.getName()).info("storeAntigenInfo() called with antigenName='" + antigenName + "' and AntigenSequence='" + antigenSequence + "'");
        customResearchProject.getResearchProject().setAntigenInfo(antigenName, antigenSequence);
        customResearchState.getResearchState().currentStep = ResearchState.Step.FIND_KNOWN_ANTIBODIES;
        Logger.getLogger(ToolsForAntigenFinder.class.getName()).info(customResearchProject.getResearchProject().toString());
    }
}