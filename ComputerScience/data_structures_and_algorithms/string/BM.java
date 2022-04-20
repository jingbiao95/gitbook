public class BM {

    private static final int SIZE = 126;

    /**
     * @param b  模式串
     * @param m
     * @param bc 存放坏字符在模式串中的最后的位置
     */
    private void generateBC(char[] b, int m, int[] bc) {

        for (int i = 0; i < SIZE; i++) {
            // 初始化bc
            bc[i] = -1;
        }
        for (int i = 0; i < m; i++) {
            // 计算b[i]的ASCII值
            int ascii = (int) b[i];
            bc[ascii] = i;
        }
    }

    /**
     * 获取能够匹配的模式串的子串起始位置
     *
     * @param a 被匹配的字符串
     * @param n
     * @param b 模式串
     * @param m
     * @return
     */
    public int bm(char[] a, int n, char[] b, int m) {
        // 记录模式串中每个字符最后出现的位置
        int[] bc = new int[SIZE];
        // 构建坏字符哈希表
        generateBC(b, m, bc);
        int[] suffix = new int[m];
        boolean[] prefix = new boolean[m];
        generateGS(b, m, suffix, prefix);

        // i表示主串与模式串对齐的第一个字符
        int i = 0;
        while (i <= n - m) {
            int j;
            // 模式串从后向前匹配
            for (j = m - 1; j >= 0; --j) {
                // 坏字符对应模式串中的下标是j
                if (a[i + j] != b[j]) break;
            }
            // 匹配成功，返回主串与模式串第一个匹配的字符位置
            if (j < 0) {
                return i;
            }
            // 这里等同于将模式串往后滑动 j -bc[(int)a[i+j]] 位
            int x = j - bc[(int) a[i + j]];
            int y = 0;
            if (j < m - 1) { // 如果有好后缀
                y = moveByGS(j, m)
            }
            // 向后移动x,y的最大值
            i = i + Math.max(x, y);
        }
        return -1;
    }

    /**
     * 获取好后缀的字符长度
     *
     * @param j      表示坏字符对应的模式串中的字符下标
     * @param m      模式串的长度
     * @param suffix
     * @param prefix
     * @return
     */
    private int moveByGS(int j, int m, int[] suffix, boolean[] prefix) {
        // 好后缀的长度
        int k = m - 1 - j;

        if (suffix[k] != -1) {
            return j - suffix[k] + 1;
        }
        // 从坏字符的右侧 [j+1,m-1] 的好后缀里有起始位置开始的字符没
        for (int r = j + 2; r < m - 1; r++) {
            // 长度为 k =  ( m-(j+1) -1) = m-j-2
            if (prefix[m - r] == true) {
                return r;
            }
        }
        return m;
    }

    /**
     * @param b      模式串
     * @param m      模式串的长度
     * @param suffix 记录后缀子串在模式串中的起始位置
     * @param prefix 记录后缀子串是否从开始位置开始的
     */
    private void generateGS(char[] b, int m, int[] suffix, boolean[] prefix) {

        for (int i = 0; i < m; i++) {
            suffix[i] = -1;
            prefix[i] = false;
        }
        // b[0,i]
        for (int i = 0; i < m - 1; i++) {
            int j = i;
            int k = 0;
            // b[0,i]与b[0,m-1]求公共后缀子串
            while (j >= 0 && b[j] == b[m - 1 - k]) {
                // 记录公共后缀子串在b[0,i]的起始位置
                suffix[k] = j;
                --j;
                k++;
            }
            if (j == -1) prefix[k] = true;
        }
    }
}