package com.example.recorder.utils;

/**
 * Format JSON strings into JSON structures
 *
 * @author lzy
 *
 */
public class JSONTool {

    public static String stringToJSON(String strJson) {
        // Count the number of tabs
        int tabNum = 0;
        StringBuffer jsonFormat = new StringBuffer();
        int length = strJson.length();

        char last = 0;
        for (int i = 0; i < length; i++) {
            char c = strJson.charAt(i);
            if (c == '{') {
                tabNum++;
                jsonFormat.append(c + "\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
            }
            else if (c == '}') {
                tabNum--;
                jsonFormat.append("\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
                jsonFormat.append(c);
            }
            else if (c == ',') {
                jsonFormat.append(c + "\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
            }
            else if (c == ':') {
                jsonFormat.append(c + " ");
            }
            else if (c == '[') {
                tabNum++;
                char next = strJson.charAt(i + 1);
                if (next == ']') {
                    jsonFormat.append(c);
                }
                else {
                    jsonFormat.append(c + "\n");
                    jsonFormat.append(getSpaceOrTab(tabNum));
                }
            }
            else if (c == ']') {
                tabNum--;
                if (last == '[') {
                    jsonFormat.append(c);
                }
                else {
                    jsonFormat.append("\n" + getSpaceOrTab(tabNum) + c);
                }
            }
            else {
                jsonFormat.append(c);
            }
            last = c;
        }
        return jsonFormat.toString();
    }

    // Space or a tab
    private static String getSpaceOrTab(int tabNum) {
        StringBuffer sbTab = new StringBuffer();
        for (int i = 0; i < tabNum; i++) {
            sbTab.append('\t');
        }
        return sbTab.toString();
    }

}