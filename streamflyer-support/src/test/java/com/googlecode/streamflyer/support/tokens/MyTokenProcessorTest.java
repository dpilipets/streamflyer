package com.googlecode.streamflyer.support.tokens;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.regex.RegexModifier;
import com.googlecode.streamflyer.support.tokens.Token;
import com.googlecode.streamflyer.support.tokens.TokenProcessor;
import com.googlecode.streamflyer.support.tokens.Tokens;

/**
 * Tests {@link MyTokenProcessor} and {@link TokenProcessor}.
 * 
 * @author rwoo
 * 
 */
public class MyTokenProcessorTest {

    /**
     * Rather an integration test than a unit test.
     * 
     * @throws Exception
     */
    @Test
    public void testProcess() throws Exception {

        // +++ define the tokens we are looking for
        List<Token> tokenList = new ArrayList<Token>();
        // we assume the whitespace is normalized so that we can use rather
        // simple regular expressions
        String regexPlainText = "[^<>]*";
        tokenList.add(new Token("SectionStart", "<section class='abc'>"));
        tokenList.add(new Token("SectionTitle", "(<h1>)(" + regexPlainText + ")(</h1>)"));
        tokenList.add(new Token("ListItem", "(<li>)(" + regexPlainText + ")(</li>)"));
        tokenList.add(new Token("SectionEnd", "</section>"));
        Tokens tokens = new Tokens(tokenList);

        // +++ create a token processor that stores the found tokens
        // and replaces some text
        MyTokenProcessor tokenProcessor = new MyTokenProcessor(tokenList);

        // +++ create the modifier
        Modifier modifier = new RegexModifier(tokens.getMatcher(), tokenProcessor, 1, 2048);

        String input = "";
        input += "text <section class='abc'>";
        input += "text <h1>my title</h1>";
        input += "text <ul>";
        input += "text <li>my first list item</li>";
        input += "text <li>my second list item</li>";
        input += "text </ul>";
        input += "text </section>";
        input += "text <h1>title outside section</h1>";
        input += "text <li>list item outside section</li>";

        // apply the modifying reader
        Reader reader = new ModifyingReader(new StringReader(input), modifier);
        String foundOutput = IOUtils.toString(reader);

        assertEquals(7, tokenProcessor.getFoundTokens().size());
        assertEquals("SectionStart:<section class='abc'>", tokenProcessor.getFoundTokens().get(0));
        assertEquals("SectionTitle:<h1>my title</h1>", tokenProcessor.getFoundTokens().get(1));
        assertEquals("ListItem:<li>my first list item</li>", tokenProcessor.getFoundTokens().get(2));
        assertEquals("ListItem:<li>my second list item</li>", tokenProcessor.getFoundTokens().get(3));
        assertEquals("SectionEnd:</section>", tokenProcessor.getFoundTokens().get(4));
        assertEquals("SectionTitle:<h1>title outside section</h1>", tokenProcessor.getFoundTokens().get(5));
        assertEquals("ListItem:<li>list item outside section</li>", tokenProcessor.getFoundTokens().get(6));

        String output = "";
        output += "text <section class='abc'>";
        output += "text <h1>TITLE_FOUND</h1>";
        output += "text <ul>";
        output += "text <li>LIST_ITEM_FOUND</li>";
        output += "text <li>LIST_ITEM_FOUND</li>";
        output += "text </ul>";
        output += "text </section>";
        output += "text <h1>title outside section</h1>";
        output += "text <li>list item outside section</li>";

        assertEquals(output, foundOutput);

    }
}