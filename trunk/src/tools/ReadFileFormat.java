package tools;

/**
 * Code taken from:
 * http://codezrule.wordpress.com/2010/03/24/extract-text-from-pdf-office-files-doc-ppt-xls-open-office-files-rtf-and-textplain-files-in-java/
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import org.apache.poi.hdf.extractor.WordDocument;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.util.LittleEndian;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;
import java.io.BufferedReader;
import java.io.FileReader;


public class ReadFileFormat {

	StringBuffer sb = new StringBuffer(8192);
	StringBuffer TextBuffer = new StringBuffer();

	public String pdftotext(String fileName) {

		PDFParser parser;
		String parsedText;
		PDFTextStripper pdfStripper;
		PDDocument pdDoc = null;
		COSDocument cosDoc = null;
		File f = new File(fileName);

		try {
			parser = new PDFParser(new FileInputStream(f));
		} catch (Exception e) {
			System.out.println("Unable to open PDF Parser.");
			return null;
		}

		try {
			parser.parse();
			cosDoc = parser.getDocument();
			pdfStripper = new PDFTextStripper();
			pdDoc = new PDDocument(cosDoc);
			parsedText = pdfStripper.getText(pdDoc);
			cosDoc.close();
			pdDoc.close();
		} catch (Exception e) {
			System.out.println("An exception occured in parsing the PDF Document.");
			e.printStackTrace();

			try {
				if (cosDoc != null) {
					cosDoc.close();
				}

				if (pdDoc != null) {
					pdDoc.close();
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}

			return null;
		}
		System.out.println("Done.");
		return parsedText;
	}

	public String doc2text(String fileName) throws IOException {
		WordDocument wd = new WordDocument(fileName);
		StringWriter docTextWriter = new StringWriter();
		wd.writeAllText(new PrintWriter(docTextWriter));
		docTextWriter.close();
		return docTextWriter.toString();
	}
	
	public String rtf2text(InputStream is) throws Exception {
		DefaultStyledDocument styledDoc = new DefaultStyledDocument();
		new RTFEditorKit().read(is, styledDoc, 0);
		return styledDoc.getText(0, styledDoc.getLength());
	}
	
	public String ppt2text(String fileName) throws Exception {
		POIFSReader poifReader = new POIFSReader();
		poifReader.registerListener(new ReadFileFormat.MyPOIFSReaderListener());
		poifReader.read(new FileInputStream(fileName));
		return sb.toString();
	}

	class MyPOIFSReaderListener implements POIFSReaderListener {
		public void processPOIFSReaderEvent(POIFSReaderEvent event) {
			char ch0 = (char) 0;
			char ch11 = (char) 11;
			try {
				DocumentInputStream dis = null;
				dis = event.getStream();
				byte btoWrite[] = new byte[dis.available()];
				dis.read(btoWrite, 0, dis.available());
				for (int i = 0; i < btoWrite.length - 20; i++) {
					long type = LittleEndian.getUShort(btoWrite, i + 2);
					long size = LittleEndian.getUInt(btoWrite, i + 4);
					if (type == 4008) {
						try {
							String s = new String(btoWrite, i + 4 + 1, (int) size + 3).replace(ch0, ' ').replace(ch11, ' ');
							if (s.trim().startsWith("Click to edit") == false) {
								sb.append(s);
							}
						} catch (Exception ee) {
							System.out.println("error:" + ee);
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public String xls2text(InputStream in) throws Exception {
		HSSFWorkbook excelWb = new HSSFWorkbook(in);
		StringBuffer result = new StringBuffer(4096);
		int numberOfSheets = excelWb.getNumberOfSheets();
		for (int i = 0; i < numberOfSheets; i++)
		{
			HSSFSheet sheet = excelWb.getSheetAt(i);
			int numberOfRows = sheet.getPhysicalNumberOfRows();
			if (numberOfRows > 0)
			{
				if (excelWb.getSheetName(i) != null && excelWb.getSheetName(i).length() != 0)
				{
					// append sheet name to content
					if (i > 0)
						result.append("\n\n");
					result.append(excelWb.getSheetName(i).trim());
					result.append(":\n\n");
				}

				Iterator<HSSFRow> rowIt = sheet.rowIterator();
				while (rowIt.hasNext())
				{
					HSSFRow row = rowIt.next();
					if (row != null)
					{
						boolean hasContent = false;
						Iterator<HSSFCell> it = row.cellIterator();
						while (it.hasNext())
						{
							HSSFCell cell = it.next();
							String text = null;
							try
							{
								switch (cell.getCellType())
								{
								case HSSFCell.CELL_TYPE_BLANK:
								case HSSFCell.CELL_TYPE_ERROR:
									// ignore all blank or error cells
									break;
								case HSSFCell.CELL_TYPE_NUMERIC:
									text = Double.toString(cell.getNumericCellValue());
									break;
								case HSSFCell.CELL_TYPE_BOOLEAN:
									text = Boolean.toString(cell.getBooleanCellValue());
									break;
								case HSSFCell.CELL_TYPE_STRING:
								default:
									text = cell.getStringCellValue();
									break;
								}
							}
							catch (Exception e){}
							if ((text != null) && (text.length() != 0))
							{
								result.append(text.trim());
								result.append(' ');
								hasContent = true;
							}
						}
						if (hasContent)
						{
							// append a newline at the end of each row that has content
							result.append('\n');
						}
					}
				}
			}
		}
		return result.toString();
	}

	@SuppressWarnings("rawtypes")
	public void processElement(Object o) {
		if (o instanceof Element) {
			Element e = (Element) o;
			String elementName = e.getQualifiedName();
			if (elementName.startsWith("text")) {
				if (elementName.equals("text:tab")) // add tab for text:tab
				{
					TextBuffer.append("\t");
				} else if (elementName.equals("text:s")) // add space for text:s
				{
					TextBuffer.append(" ");
				} else {
					List children = e.getContent();
					Iterator iterator = children.iterator();
					while (iterator.hasNext()) {
						Object child = iterator.next();
						//If Child is a Text Node, then append the text
						if (child instanceof Text) {
							Text t = (Text) child;
							TextBuffer.append(t.getValue());
						} else {
							processElement(child); // Recursively process the child element
						}
					}
				}
				if (elementName.equals("text:p")) {
					TextBuffer.append("\n");
				}
			} else {
				List non_text_list = e.getContent();
				Iterator it = non_text_list.iterator();
				while (it.hasNext()) {
					Object non_text_child = it.next();
					processElement(non_text_child);
				}}}}


	@SuppressWarnings("rawtypes")
	public String getOpenOfficeText(String fileName) throws Exception {
		TextBuffer = new StringBuffer();
		//Unzip the openOffice Document
		ZipFile zipFile = new ZipFile(fileName);
		Enumeration entries = zipFile.entries();
		ZipEntry entry;
		while (entries.hasMoreElements()) {
			entry = (ZipEntry) entries.nextElement();
			if (entry.getName().equals("content.xml")) {
				TextBuffer = new StringBuffer();
				SAXBuilder sax = new SAXBuilder();
				Document doc = sax.build(zipFile.getInputStream(entry));
				Element rootElement = doc.getRootElement();
				processElement(rootElement);
				break;
			}
		}
		return TextBuffer.toString();
	}
	
	public String fileToStringNow(File f) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(f));
		String nextLine = "";
		StringBuffer sbuff = new StringBuffer();
		while ((nextLine = br.readLine()) != null) {
			sbuff.append(nextLine);
			sbuff.append(System.getProperty("line.separator"));
		}
		return sbuff.toString();
	}

	public static String readFile(File f) {
		if(f != null && f.exists()) {
			ReadFileFormat rff = new ReadFileFormat();
			String fileName = f.getAbsolutePath();
			
			try {
				if (!f.exists()) {
					return null;
				} else {
					if (f.getName().endsWith(".pdf") || f.getName().endsWith(".PDF")) {
						return rff.pdftotext(fileName);
					} else if (f.getName().endsWith(".doc") || f.getName().endsWith(".DOC")) {
						return rff.doc2text(fileName);
					} else if (f.getName().endsWith(".rtf") || f.getName().endsWith(".RTF")) {
						return rff.rtf2text(new FileInputStream(f));
					} else if (f.getName().endsWith(".ppt") || f.getName().endsWith(".PPT")) {
						return rff.ppt2text(fileName);
					} else if (f.getName().endsWith(".xls") || f.getName().endsWith(".XLS")) {
						return rff.xls2text(new FileInputStream(f));
					} else if (f.getName().endsWith(".odt") || f.getName().endsWith(".ODT") || f.getName().endsWith(".ods") || f.getName().endsWith(".ODS") || f.getName().endsWith(".odp") || f.getName().endsWith(".ODP")) {
						return rff.getOpenOfficeText(fileName);
					} else {
						return rff.fileToStringNow(f);
					}
				}
			} catch(Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}	
}
