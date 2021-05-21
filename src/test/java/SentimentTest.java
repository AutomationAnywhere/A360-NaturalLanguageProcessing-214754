import com.automationanywhere.botcommand.NLP.NamedEntityRecognition;
import com.automationanywhere.botcommand.NLP.SentimentAnalysis;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.NumberValue;
import com.automationanywhere.botcommand.data.model.table.Table;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SentimentTest {
    @Test
    public void testSentiment(){
        //Testing with String because the "NumberValue" isnt easily converted to other types
        String expectedOutput= "0.66666666666666662965923251249478198587894439697265625";
        //One Neutral Sentence, Two Positive Sentences
        String text = "Hello this is Micah. I love building new packages for the Automation Anywhere community. " +
                "Its always great to see organizations finding cool uses for them.";
        SentimentAnalysis sentimentAnalysis = new SentimentAnalysis();

        NumberValue sentiment = sentimentAnalysis.action(text);
        System.out.println(sentiment);
        Assert.assertEquals(String.valueOf(sentiment), expectedOutput);

    }
}
