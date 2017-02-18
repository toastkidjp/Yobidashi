package jp.toastkid.libs.calculator;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * calculate reverse polish notation.
 * @author Toast kid
 * @see <a href="http://qiita.com/Liberty/items/1811bc336fe31ae0c8da">
 * Javaで逆ポーランド記法（計算編）</a>
 * @see <a href="https://ja.wikipedia.org/wiki/逆ポーランド記法">逆ポーランド記法</a>
 */
public final class ReversePolish {

    /** space splitter. */
    private static final String SPACE_SPLITTER = "\\s";

    /**
     * Private constructor.
     */
    private ReversePolish() {
        // NOP.
    }

    /**
     * Calculate by reverse polish notation use String input.
     * @param input string array. ex: "1 2 3 - +"
     * @return integer. ex: input "1 2 3 - +", return 0.
     */
    public static int calculate(final String input) {
        return calculate(input.split(SPACE_SPLITTER));
    }

    /**
     * Calculate by reverse polish notation use String input.
     * @param input string array.
     * @return integer.
     */
    public static int calculate(final String... input) {

        final Deque<Integer> que = new ArrayDeque<>(input.length);
        int a = 0;
        int b = 0;
        for (final String c : input) {
            switch (c) {
            case "+":
                a = que.pollFirst();
                b = que.pollFirst();
                que.addFirst(b + a);
                break;
            case "-":
                a = que.pollFirst();
                b = que.pollFirst();
                que.addFirst(b - a);
                break;
            case "/":
                a = que.pollFirst();
                b = que.pollFirst();
                que.addFirst(b / a);
                break;
            case "*":
                a = que.pollFirst();
                b = que.pollFirst();
                que.addFirst(b * a);
                break;
            default:
                que.addFirst(Integer.parseInt(c));
            }
        }
        return que.pop();
    }

}
