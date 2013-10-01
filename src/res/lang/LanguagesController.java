package res.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class LanguagesController {

    private Map<String,Locale> supportedLanguages;
    private ResourceBundle translation;

    public LanguagesController(String language){
        supportedLanguages = new HashMap<String,Locale>();
        supportedLanguages.put("English", Locale.ENGLISH);

        translation = ResourceBundle.getBundle("res/lang/language", supportedLanguages.get(language));
    }

    public String getWord(String keyword)
    {
        return translation.getString(keyword);
    }

}