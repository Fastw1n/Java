public class SumLong {
    public static void main(String[] args) {
        long sum = 0;

        for (int i = 0; i < args.length; i++) {
            int start = 0;
            int end = 0;
            for (int l = 0; l < args[i].length(); l++) {
                if (Character.isWhitespace(args[i].charAt(l))) {
                    if (start < end) {
                        sum += Long.parseLong(args[i].substring(start, l));
                    }
                    start = l + 1;
                    end = l;
                } else {
                    end = l + 1;


                }
            }
            if (start < end) {
                sum += Long.parseLong(args[i].substring(start, end));
            }
        }
        System.out.println(sum);
    }

}