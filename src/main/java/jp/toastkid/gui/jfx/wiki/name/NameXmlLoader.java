package jp.toastkid.gui.jfx.wiki.name;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.concurrent.Callable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import jp.toastkid.libs.utils.FileUtil;

/**
 * Read nameInformation from passed XML.
 * @author Toast kid
 *
 */
public class NameXmlLoader implements Callable<Collection<NameInformation>>{

    private final Collection<NameInformation> names;
    private final Collection<String> nationalities;

    private final File targetFile;

    private Document document;

    /**
     *
     * @param file
     */
    public NameXmlLoader(final File file) {
        this.targetFile = file;
        nationalities = Sets.mutable.empty();
        names         = Lists.mutable.empty();
    }

    @Override
    public Collection<NameInformation> call() {
        callConectingXML();
        return names;
    }

    /**
     * XML 接続メソッドを呼び出す.
     * @param targetFile
     */
    public void callConectingXML() {
        try {
            final URLConnection connection
                = new URL("file:///" + targetFile.getAbsolutePath()).openConnection();
            connection.connect();
            try (final BufferedReader in = FileUtil.openReader(connection.getInputStream());) {
                final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder builder = factory.newDocumentBuilder();
                document = builder.parse(new InputSource(in));
                in.close();
            } catch (ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }
            // root要素 (RDF or rss) を得る.
            walk(document.getDocumentElement());
        } catch(final SecurityException | IOException se) {
            se.printStackTrace();
        }
    }

    /**
     * ファイルを読み込む
     */
    private void walk(final Node n) {
        for (Node ch = n.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
            if (ch.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            names.add(processItem(ch));
            //walk(ch);
        }
    }

    /**
     * XMLの行を処理する
     * @param tempNode
     * @return NameInformation
     */
    private NameInformation processItem(final Node tempNode) {
        final NameInformation.Builder builder = new NameInformation.Builder();
        for (Node ch = tempNode.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
            if (ch.getNodeType() == Node.ELEMENT_NODE) {
                final String nodeName = ch.getNodeName();
                if ("name".equals(nodeName) ) {
                    builder.setName(getText(ch));
                } else if ("spelling".equals(nodeName)    ) {
                    builder.setSpelling(getText(ch));
                } else if ("nationality".equals(nodeName) ) {
                    builder.setNationality(getText(ch));
                    nationalities.add(getText(ch));
                } else if ("seibetsu".equals(nodeName)    ) {
                    builder.setSeibetsu(getText(ch));
                }
            }
        }
        return builder.build();
    }
    /**
     * ノードを指定して内部の文字列を取り出す
     * @param n ノード
     * @return content
     */
    private String getText(final Node n) {
        String content = "";
        for (Node ch = n.getFirstChild(); ch != null; ch = ch.getNextSibling()) {
            if (ch.getNodeType() == Node.ELEMENT_NODE) {
                content += getText(ch);
            }
            else if (ch.getNodeType() == Node.TEXT_NODE
                    && ch.getNodeValue().trim().length() != 0) {
                content += ch.getNodeValue();
            }
            else if (ch.getNodeType() == Node.CDATA_SECTION_NODE) {
                content += ch.getNodeValue();
            }
        }
        return content;
    }

    public Collection<String> getNationalities() {
        return nationalities;
    }

}
