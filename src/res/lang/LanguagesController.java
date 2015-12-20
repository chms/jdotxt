/**
* Copyright (C) 2013-2015 Christian M. Schmid
*
* This file is part of the jdotxt.
*
* PILight is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

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