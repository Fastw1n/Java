package search;

import java.util.ArrayList;
import java.util.List;


public class BinarySearchMissing {

    //присваиване
    public static List<Integer> arrays;
    public static int x;

    //Pred : True
    public static void main(String[] args) {
        //присваиване
        arrays = new ArrayList<>();
//        Pred: args[0] != null
        x = Integer.parseInt(args[0]);
//        Post : x && x != null
//        Pred : args.lenght >= 1
        for (int i = 1; i < args.length; i++) {
//            Pred : i < args.length
            arrays.add(Integer.parseInt(args[i]));
//            Post : arrays.size()' = arrays.size() + 1 && arrays.size()' == Integer.parseInt(args[i])
        }
//        Pred : True
        System.out.println(searchBinaryRecursive(-1, arrays.size(), x));
//        Post : right && True
    }
    private static int binarySearch() {
        int left = -1;
        int right = arrays.size();
        int med;
//        Pred : left < right
        while(left < right) {
//            Pred && Post :left < right
            if (left + 1 == right) {
//                Post : left + 1 == right && left < right
//                Pred : True
                break;
//                Post : left < right
            }
//            Pred : True
            med = (right + left) / 2;
//            Post : med = (right + left) / 2
//            Pred : med = (right + left) / 2 && med >= 0 && med < arrays.size()
            if (arrays.get(med) < x) {
//                Post : arrays.get(med) < x
//                Pred : True
                left = med;
//                Post : left = med && left < right
            } else {
//                Post : arrays.get(med) >= x
//                Pred : True
                right = med;
//                Post : right = med && left > right
            }
        }
//        Pred : left < right
        if ( right!= arrays.size() && arrays.get(right) == x) {
//            Post : right!= arrays.size() && arrays.get(right) == x
            return right;
//            Post : R = right && right < arrays.size() && right >= 0
        }
//        Pred : True
        return ((-right) - 1);
//        Post :  R = (-right) - 1
    }

    // 1 2 3 4 5 5 5 7 8
    public static int searchBinaryRecursive(int left, int right,int x) {
            //Pred : True
            if (left + 1 == right) {
//            Post : left + 1 == right
                if ( right!= arrays.size() && arrays.get(right) == x) {
//                    Post : left + 1 == right &&  right!= arrays.size() && arrays.get(right) == x
//                    Pred : True
                    return right;
//                    Post : R = right && right >= 0 && right < arrays.size()
                }
//                Pred : True
                return ((-right) - 1);
//                Post : R = ((-right) - 1)
            }
            int med = (right + left) / 2;
//            Post : arrays.get(med) < x && med >= 0 && med < arrays.size()
            if (arrays.get(med) < x) {
//                Post : arrays.get(med) < x
                 return searchBinaryRecursive( med, right, x);
            }
//        Post : arrays.get(med) >= x
        return searchBinaryRecursive( left, med, x);
    }
}
/*
5 | 1 2 3 4 6 7
-2334 | 1 2 3 4 4 5
0
2435353 | 1 2 3 4  5
-6
i -1 * (i + 1)
*/