package com.automationanywhere.botcommand.NLP;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.*;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXTAREA;
import static com.automationanywhere.commandsdk.model.DataType.*;

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be displayable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "NamedEntityRecognition", label = "[[NamedEntityRecognition.label]]",
        node_label = "[[NamedEntityRecognition.node_label]]", description = "[[NamedEntityRecognition.description]]", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[NamedEntityRecognition.return_label]]", return_type = TABLE, return_required = true, return_description = "[[NamedEntityRecognition.return_label.description]]")
public class NamedEntityRecognition {
    //Identify the entry point for the action. Returns a Value<String> because the return type is String.
    @Execute
    public Value<Table> action(
            //Idx 1 would be displayed first, with a text box for entering the value.
            @Idx(index = "1", type = TEXTAREA)
            //UI labels.
            @Pkg(label = "[[NamedEntityRecognition.textBody.label]]")
            //Ensure that a validation error is thrown when the value is null.
            @NotEmpty
                    String textBody) {

        //Internal validation, to disallow empty strings. No null check needed as we have NotEmpty on firstString.
        if ("".equals(textBody.trim()))
            throw new BotCommandException("Please be sure to include some text for the named entity recognition to run properly.");


        //Business logic
        //CreateTable
        Table nerResult = new Table();
        List<Schema> header = new ArrayList<Schema>();
        header.add(new Schema("id"));
        header.add(new Schema("text"));
        header.add(new Schema("entity"));
        nerResult.setSchema(header);

        //Set NLP Pipeline
        String propertiesName = "tokenize,ssplit,pos,lemma, ner";
        Properties properties = new Properties();
        properties.setProperty("annotators", propertiesName);
        StanfordCoreNLP stanfordCoreNLP = new StanfordCoreNLP(properties);

        List<Row> allRows = new ArrayList<Row>();

        //Break down text into entities
        CoreDocument coreDocument = new CoreDocument(textBody);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabels = coreDocument.tokens();
        //Set Counter for ID column
        int currentRowCounter = 1;
        try{
            for(CoreLabel coreLabel : coreLabels){
                //Add values to row
                String ner = coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                List<Value> currentRow = new ArrayList<>();
                currentRow.add(new StringValue(String.valueOf(currentRowCounter)));
                currentRow.add(new StringValue(coreLabel.originalText()));
                currentRow.add(new StringValue(ner));

                //set current row data to row
                Row row = new Row();
                row.setValues(currentRow);
                //Commit row to All Rows
                allRows.add(row);
                //Increment Counter
                currentRowCounter++;
            }
            //Add allRows to Table
            nerResult.setRows(allRows);
        }catch (Exception e){
            throw new BotCommandException("Error in performing Named Entity Recognition: " + e.toString());
        }

        //Return Table Value
        return new TableValue(nerResult);
    }
}
