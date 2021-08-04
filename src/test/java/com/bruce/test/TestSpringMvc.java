package com.bruce.test;

//import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class TestSpringMvc {

   // @Test
    public void testReadXMl() {
        //String basePackage = Xmlparser.getBasePackage("springmvc.xml");
       // System.out.println(basePackage);
    }


    //@Test
    public void testSXcript() {
        String str = "Json对象转换为XML对象。 \n" +
                "\n" +
                "基本用法：\n" +
                " var res = String.json2Xml(arg);\n" +
                "\n" +
                "入参说明：\n" +
                "a$rg ： 输入参数为json对象，例如： {id:1 ,name:'daivi',age:3 }\n" +
                "\n" +
                "结果说明：\n" +
                "res : 输出结果为XML对象\n" +
                "\n" +
                "注意事项：\n" +
                "无\n" +
                "\n" +
                "代码示例：\n" +
                "//假设有一个json字符串如下\n" +
                "var str =  {id:1 ,name:'daivi',age:3 };\n" +
                "//将字符串转换为xml\n" +
                "var xml= String.json2Xml(str);\n" +
                "\n" +
                "输出结果：\n" +
                "\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"no\\\"?>\n" +
                "<data><id>1</id><name>daivi</name><age>3</age></data>\"\n" +
                "\n" +
                "\n" +
                "代码示例2：\n" +
                "//假设有一个json字符串如下\n" +
                "var str =  {id:1 ,name:'daivi',age:3 };\n" +
                "//将字符串转换为xml\n" +
                "var xml= String.json2Xml(str); \n" +
                "//可以通过如下方式访问xml中的标签值\n" +
                "var result = xml.#children[1].#text;   \n" +
                "\n" +
                "输出结果：\n" +
                "  \"daivi\"";

        parseAndAddLabel(str);


    }

    private void parseAndAddLabel(String str) {
        StringReader reader = new StringReader(str);
        BufferedReader bufferedReader = new BufferedReader(reader);
        int i = 0;
        String s;
        StringBuilder sbuffer = new StringBuilder();
        try {
            while ((s = bufferedReader.readLine()) != null) {
                s = s.trim();
                char c;
                int index;
                int index1;
                int index2;
                if (s.startsWith("基本用法") || s.startsWith("入参说明") || s.startsWith("结果说明") || s.startsWith("注意事项")
                        || s.startsWith("代码示例") || s.startsWith("输出结果")) {
                    sbuffer.append("<m>").append(s).append("</m>\n");
                } else if (s.startsWith("//")) {
                    sbuffer.append("<g>").append(s).append("</g>\n");
                } else if ("".equals(s)) {
                    sbuffer.append("\n");
                } else if (s.startsWith("var ")) {
                    String vary = s.substring(0, 3);
                    String lastStr = s.substring(3);
                    int index3 = lastStr.indexOf("=");
                    if (index3 > 0) {
                        sbuffer.append("<r>").append(vary).append("</r>");
                        String ref = lastStr.substring(0, index3);
                        if (ref.trim().matches("^[a-zA-Z_$][a-zA-Z0-9_$]*$")) {
                            sbuffer.append(" <b>").append(ref.trim()).append("</b> ");
                        } else {
                            // 不合法的变量名
                            sbuffer.append(ref).append("\n");
                        }
                        sbuffer.append(lastStr.substring(index3)).append("\n");

                    }
                } else if (('A' <= (c = s.charAt(0)) && c <= 'Z' || 'a' <= c && c <= 'z' || c == '$' || c == '_') &&
                        ((index1 = (index = s.indexOf(":")) == -1 ? (s.indexOf("：")) : ((index2 = s.indexOf("："))
                                == -1 ? index : Math.min(index, index2)))) > 0) {

                    String variable = s.substring(0, index1);
                    if (variable.trim().matches("^[a-zA-Z_$][a-zA-Z0-9_$]*$")) {
                        sbuffer.append("<b>").append(variable).append("</b>");
                    } else {
                        sbuffer.append(variable);
                    }
                    String lastStr = s.substring(index1);
                    sbuffer.append(lastStr).append("\n");
                    // }
                } else {
                    sbuffer.append(s).append("\n");
                }
            }
            System.out.println(sbuffer.toString());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   // @Test
    public void test() {
        String str = "232fd：fd";
        System.out.println(str.indexOf("2"));
    }
}
