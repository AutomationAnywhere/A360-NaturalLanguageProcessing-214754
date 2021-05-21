package com.automationanywhere.botcommand.NLP;

import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.List;
import java.util.Properties;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXTAREA;
import static com.automationanywhere.commandsdk.model.DataType.NUMBER;

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be displayable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "SentimentAnalysis", label = "[[SentimentAnalysis.label]]",
        node_label = "[[SentimentAnalysis.node_label]]", description = "[[SentimentAnalysis.description]]", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[SentimentAnalysis.return_label]]", return_type = NUMBER, return_required = true, return_description = "[[SentimentAnalysis.return_label.description]]")
public class SentimentAnalysis {

    //Identify the entry point for the action. Returns a Value<String> because the return type is String.
    @Execute
    public NumberValue action(
            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "1", type = TEXTAREA)
            //UI labels.
            @Pkg(label = "[[SentimentAnalysis.textBody.label]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    String textBody) {

        //Internal validation, to disallow empty strings. No null check needed as we have NotEmpty on firstString.
        if ("".equals(textBody.trim()))
            throw new BotCommandException("Please be sure to include some text for the sentiment analysis to run properly.");


        //Business logic
        Double result = 0.0;
        String propertiesName = "tokenize,ssplit,pos,lemma, ner, parse, dcoref, sentiment";
        Properties properties = new Properties();
        properties.setProperty("annotators", propertiesName);
        StanfordCoreNLP stanfordCoreNLP = new StanfordCoreNLP(properties);


        CoreDocument coreDocument = new CoreDocument(textBody);

        stanfordCoreNLP.annotate(coreDocument);

        //Break down text body into sentences
        List<CoreSentence> sentences;
        try{
            sentences = coreDocument.sentences();
        } catch (Exception e) {
            throw new BotCommandException("Error breaking text into sentences. Validate input text body structure");
        }

        //Create score for sentiment
        Double sentenceCount = Double.valueOf(sentences.size());
        Double scoreOfSentences = 0.0;
        for(CoreSentence sentence: sentences){
            if (sentence.sentiment().contains("Positive")){
                //+1 for Positive or Very Positive
                scoreOfSentences = scoreOfSentences+1;
            }else if (sentence.sentiment().contains("Negative")){
                //-1 for Negative or Very Negative
                scoreOfSentences = scoreOfSentences-1;
            }
        }

        if(scoreOfSentences == 0.0){
            result = 0.0;
        } else{
            result = scoreOfSentences/sentenceCount;
        }

        //Return StringValue.
        return new NumberValue(result);
    }
}
