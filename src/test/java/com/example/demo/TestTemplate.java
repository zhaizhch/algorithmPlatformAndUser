package com.example.demo;

import org.springframework.stereotype.Component;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.sql.SQLOutput;
@Component
public class TestTemplate {
    public String test(){
        STGroup stgroup = new STGroupFile("D:\\推理平台\\algorithmPlatformAndUser\\src\\test\\java\\com\\example\\demo\\test.stg");
        ST st = stgroup.getInstanceOf("decl");
        st.add("baseImage","int");
        st.add("userPath","x");
        st.add("existsUserRequirement",true);
        String result = st.render();
        System.out.println(result);
        return result;
    }


}
