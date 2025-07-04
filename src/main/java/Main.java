public class Main {
    public static void main(String[] args) {
        String resultPath = ".";
        String prefix = "";
        boolean append = false;
        StatType statType = StatType.NO_STAT;

        boolean paramsCondition = true;
        int index = 0;
        while (paramsCondition) {
            switch (args[index]) {
                case ("-o"):
                    try {
                        resultPath = args[index + 1];
                        index += 1;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("You used the '-o' flag but did not enter a path for the result.");
                    }
                    break;
                case ("-p"):
                    try {
                        prefix = args[index + 1];
                        index += 1;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("You used the '-p' flag but did not enter a prefix.");
                    }
                    break;
                case ("-s"):
                    if (statType != StatType.NO_STAT) {
                        System.out.println("You entered two flags to output statistics. " +
                                "The '-s' flag will be ignored.");
                    } else {
                        statType = StatType.SHOR;
                    }
                    break;
                case ("-f"):
                    if (statType != StatType.NO_STAT) {
                        System.out.println("You entered two flags to output statistics. " +
                                "The '-f' flag will be ignored.");
                    } else {
                        statType = StatType.FULL;
                    }
                    break;
                case ("-a"):
                    append = true;
                    break;
                default:
                    if (!args[index].matches(".+\\.txt")) {
                        System.out.println("All files must come after the flags and must have the extension .txt");
                    }
                    paramsCondition = false;
                    continue;
            }
            index += 1;
        }

        FileManager fileManager = new FileManager(resultPath, prefix, append);
        for (int i = index; i < args.length; i++) {
            if (!args[i].matches(".+\\.txt")) {
                System.out.println("File " + args[i] + " skipped due to invalid extension");
                continue;
            }
            fileManager.filterFile(args[i]);
        }

        fileManager.printStat(statType);
    }
}