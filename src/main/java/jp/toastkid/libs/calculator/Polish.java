package jp.toastkid.libs.calculator;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * calculate reverse polish notation.
 * @author Toast kid
 *
 * @see <a href="http://qiita.com/Liberty/items/1811bc336fe31ae0c8da">
 * Javaで逆ポーランド記法（計算編）</a>
 * @see <a href="https://ja.wikipedia.org/wiki/逆ポーランド記法">逆ポーランド記法</a>
 */
public class Polish {

    /** space splitter. */
    private static final String SPACE_SPLITTER = "\\s";

    /**
     * Private constructor.
     */
    private Polish() {
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

        final Deque<Integer> result = new ArrayDeque<>(input.length);
        final Deque<Integer> ints = new ArrayDeque<>(2);
        final Deque<String> que = new ArrayDeque<>(input.length);
        for (final String c : input) {
            switch (c) {
                case "+":
                case "-":
                case "/":
                case "*":
                    que.add(c);
                    break;
                default:
                    ints.add(Integer.parseInt(c));
                    if (2 <= ints.size()) {
                        result.add(calc(que.pollLast(), ints.pollLast(), ints.pollLast()));
                    }
            }
        }
        for (final String operator : que) {
            result.add(calc(operator, result.pollLast(), result.pollLast()));
        }
        return result.poll();
    }
    /**
     * calc simple.
     * @param operator operator.
     * @param a int a.
     * @param b int b.
     * @return calculate result.
     */
    private static final int calc(final String operator, final int a, final int b) {
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "/":
                return a / b;
            case "*":
                return a * b;
        }
        return 0;
    }
}
