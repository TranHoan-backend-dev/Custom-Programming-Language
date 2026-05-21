package verification;

public class VerifyAll {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("RUNNING ALL CPL VERIFICATION TESTS");
        System.out.println("================================================================================\n");

        Verify01EntryPoint.main(args);
        Verify02Output.main(args);
        Verify03Comments.main(args);
        Verify04DataTypes.main(args);
        Verify05VariablesConstants.main(args);
        Verify06Operators.main(args);
        Verify07IfElse.main(args);
        Verify08SwitchCase.main(args);
        Verify09Loops.main(args);
        Verify10Functions.main(args);
        Verify11Strings.main(args);

        System.out.println("================================================================================");
        System.out.println("ALL CPL VERIFICATION TESTS COMPLETED");
        System.out.println("================================================================================");
    }
}
