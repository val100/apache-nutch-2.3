package net.iiit.siel.analysis.lang;
// Commons Logging imports
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Nutch imports
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.HTMLMetaTags;
import org.apache.nutch.parse.Parse;


//import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.protocol.Content;

// Hadoop imports
import org.apache.hadoop.conf.Configuration;

// DOM imports
import org.w3c.dom.DocumentFragment;
//Additional
import org.apache.avro.util.Utf8;
import java.nio.ByteBuffer;
import org.apache.nutch.storage.WebPage.Field;
import java.util.*;
import org.apache.nutch.storage.WebPage;
import org.apache.nutch.parse.ParseFilter;

//public class LanguageParseFilter implements HtmlParseFilter {
public class LanguageParseFilter implements ParseFilter {
  public static final Log LOG = LogFactory.getLog(LanguageParseFilter.class);
  
  private Configuration conf = null;
  private LanguageIdentifier languageIdentifier;
  private static final Collection<WebPage.Field> FIELDS = new HashSet<WebPage.Field>();
  public LanguageParseFilter() {
	 
  }
  
  /**
   * Scan the HTML document looking at possible indications of content language<br>
   * <li>1. html lang attribute (http://www.w3.org/TR/REC-html40/struct/dirlang.html#h-8.1)
   * <li>2. meta dc.language (http://dublincore.org/documents/2000/07/16/usageguide/qualified-html.shtml#language)
   * <li>3. meta http-equiv (content-language) (http://www.w3.org/TR/REC-html40/struct/global.html#h-7.4.4.2)
   * <br>Only the first occurence of language is stored.
   */
  //public ParseResult filter(Content content, ParseResult parse, HTMLMetaTags metaTags, DocumentFragment doc) {
    public Parse filter(String url, WebPage page, Parse parse, HTMLMetaTags metaTags,
      DocumentFragment doc){
    //String url = content.getUrl();
    //String parseText = parse.get(url).getData().getTitle() + parse.get(url).getText();
    String parseText = parse.getTitle() + parse.getText();
    try {
    	String lang = (languageIdentifier.getLanguage(url, parseText)).toString();
    	//parse.get(url).getData().getParseMeta().set(Metadata.LANGUAGE, lang);
      page.getMetadata().put(new Utf8(Metadata.LANGUAGE),
          ByteBuffer.wrap(lang.getBytes()));
    }
    catch (UnsupportedLanguageException e) {
    	//parse.get(url).getData().getParseMeta().set(Metadata.LANGUAGE, Language.UNSUPPORTED.toString());
      page.getMetadata().put(new Utf8(Metadata.LANGUAGE),
          ByteBuffer.wrap(Language.UNSUPPORTED.toString().getBytes()));
    }
    
    return parse;
  }
    
    /**
     * Parse a language string and return an ISO 639 primary code,
     * or <code>null</code> if something wrong occurs, or if no language is found.
     */
    
    
  

  public void setConf(Configuration conf) {
    this.conf = conf;
    this.languageIdentifier = LanguageIdentifier.getInstance(conf);
  }

  public Configuration getConf() {
    return this.conf;
  }
  public Collection<Field> getFields() {
    return FIELDS;
  }
}
