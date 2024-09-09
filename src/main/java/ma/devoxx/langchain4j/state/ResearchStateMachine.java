package ma.devoxx.langchain4j.state;

public class ResearchStateMachine {

    public static String getCurrentStep(ResearchProject project) {
        return project.currentStep;
    }

    public static void moveToNextStep(ResearchProject project) {
        switch (getCurrentStep(project)) {
            case ResearchProject.STEP1:
                project.currentStep = ResearchProject.STEP2;
                break;
            case ResearchProject.STEP2:
                project.currentStep = ResearchProject.STEP3;
                break;
            case ResearchProject.STEP3:
                project.currentStep = ResearchProject.STEP4;
                break;
            case ResearchProject.STEP4:
                project.currentStep = ResearchProject.STEP5;
                break;
            case ResearchProject.STEP5:
                project.currentStep = ResearchProject.STEP6;
                break;
            case ResearchProject.STEP6:
                project.currentStep = ResearchProject.FINISHED;
                break;
            default:
                throw new IllegalStateException("Unknown step: " + getCurrentStep(project));
        }
    }
}