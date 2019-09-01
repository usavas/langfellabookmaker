package com.langfella.bookmaker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.imageio.ImageIO;

public class App 
{
	private static final String DEFAULT_BOOK_DIR_SUFFIX = "_Audio";
	/**
	 * this folder has to contain the book txt files and (if exists) the folder containing audio and image
	 */
	private static final String INPUT_DIR = "/home/savas/Desktop/books";
	private static final String OUTPUT_DIR = "/home/savas/Desktop/output";
	private static final String OUTPUT_DIR_IMAGES = Paths.get(OUTPUT_DIR, "images").toString();
	private static final String OUTPUT_JSON_FILE_PATH = Paths.get(OUTPUT_DIR, "books.json").toString();
	private static final String OUTPUT_JSON_MIN_FILE_PATH = Paths.get(OUTPUT_DIR, "books_min.json").toString();
	private static final String DEFAULT_IMAGE_FILE_NAME = "cover.jpg";
	private static final String DEFAULT_TEXT_SUFFIX = ".txt";
	private static final String DEFAULT_WEB_ADDRESS = "https://english-e-reader.net/book/";

    public static void main( String[] args ) throws IOException {

    	getAndWriteAllBooksToJsonFile();

    }

    private static  void getAndWriteAllBooksToJsonFile() throws IOException {
		if (Files.exists(Paths.get(OUTPUT_DIR))) {
			FileUtils.deleteDirectory(new File(OUTPUT_DIR));
		}
		Files.createDirectories(Paths.get(OUTPUT_DIR));
		Files.createDirectories(Paths.get(OUTPUT_DIR_IMAGES));

		File mainFolder = new File(INPUT_DIR);

		ArrayList<String> bookFileNames = getFileNamesFromFolder(mainFolder);

		ArrayList<Book> books = new ArrayList<Book>();
		for(String bookFileName : bookFileNames) {

			try {
				String fileNameWithoutExtension = bookFileName.replace(DEFAULT_TEXT_SUFFIX, "");

				Book b = new Book();

				setBookTitleFromFileName(b, fileNameWithoutExtension);
				setBookAuthorFromFileName(b, fileNameWithoutExtension);

				//TODO get book contents from text file
				setBookContentFromFile(b, Paths.get(mainFolder.toPath().toString(), bookFileName));

				//TODO then if exists the book cover in local file get image
				File bookFolderName = new File(Paths.get(mainFolder.toPath().toString(), fileNameWithoutExtension + DEFAULT_BOOK_DIR_SUFFIX).toString());
				if(bookFolderName.exists()) {
					ImageIO.write(getBookCover(Paths.get(bookFolderName.toPath().toString(), DEFAULT_IMAGE_FILE_NAME).toString()),
							"jpg", new File(Paths.get(OUTPUT_DIR_IMAGES, fileNameWithoutExtension + ".jpg").toString()));
				}

				//TODO then add the meta info from web and get image if not exists locally
				setBookDetailsFromWeb(b, bookFolderName.toPath(), fileNameWithoutExtension);

				books.add(b);
				System.out.println("completed: " + b.toString());
			}catch (Exception e){
			}

		}

		WriteAsJsonToFile(books, OUTPUT_JSON_FILE_PATH);

		// TODO set excerpt and clear chapters and run to min
		books
				.forEach(b-> {
					if (b.getChapters().get(0).length() >= 200 ){
						b.setExcerpt(b.getChapters().get(0).substring(0, 200) + "...");
					} else {
						b.setExcerpt(b.getChapters().get(0));
					}
					b.setChapters(null);
				});

		WriteAsJsonToFile(books, OUTPUT_JSON_MIN_FILE_PATH);
	}

	private static void WriteAsJsonToFile(ArrayList<Book> books, String outputJsonFilePath) throws IOException {
		Gson gson = new Gson();
		Type listType = new TypeToken<ArrayList<Book>>(){}.getType();
		String jsonBooks = gson.toJson(books, listType);
		FileUtils.write(new File(outputJsonFilePath), jsonBooks, StandardCharsets.UTF_8);
	}

	private static void setBookContentFromFile(Book book, Path pathToBook) {
    	String content = readFile(pathToBook.toString());

    	while (content.contains("\n\n")
    			|| content.contains("\r\n")) {
    		content = content.replaceAll("\n\n", "\n")
    		.replaceAll("\r\n", "\n");
    	}
    	book.setChapters(makeChapters(pathToBook));
    }
    
    private static ArrayList<String> makeChapters(Path pathToBook) {
    	
    	ArrayList<String> chapters = new ArrayList<String>();
    	
    	StringBuilder buildIfNoChapter = new StringBuilder();
    	
    	File file = new File(pathToBook.toString());
    	
    	boolean chaptersStarted = false;
    	StringBuilder currChapter = new StringBuilder();
    	
    	try {
			for (String l: FileUtils.readLines(file, StandardCharsets.UTF_8)){
				
				if(l.toLowerCase().contains("chapter") && (l.split(" ").length == 2 || l.split(" ").length == 3)) {
					chaptersStarted = true;
					if (!currChapter.toString().equals("")) {
						chapters.add(currChapter.toString());
						currChapter = new StringBuilder();
					}
				} else {
					if (chaptersStarted) {
						if (!l.trim().equals("")){
							currChapter.append("\n").append(l);
						}
					} else {
						buildIfNoChapter.append(l);
					}
				}				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	if (!chapters.isEmpty()) {
        	return chapters;    		
    	} else {
    		return new ArrayList<String>(Collections.singletonList(buildIfNoChapter.toString()));
    	}
    }
    
    private static String readFile(String fileFullPath) {
        File file = new File(fileFullPath);
        String fileContent = "";
        try {
			fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}        
        return fileContent;
    }    
    
    private static void setBookTitleFromFileName(Book book, String fileNameWithoutExtension) {
    	book.setTitle(((String)Array.get(fileNameWithoutExtension.split("-"), 0)).replace("_", " "));
    }
    
    private static void setBookAuthorFromFileName(Book book, String fileNameWithoutExtension) {
    	book.setAuthor(((String)Array.get(fileNameWithoutExtension.split("-"), 1)).replace("_", " "));
    }
    
    private static void setBookDetailsFromWeb(Book book, Path bookFolderName, String bookFileName) {
    	
		String url = String.format("%s%s",
				DEFAULT_WEB_ADDRESS,
				bookFileName.replace(".txt", "").replace("_", "-").toLowerCase());

		try {
			Document doc = Jsoup.connect(url).get();
			//TODO manipulate web content
			Element mainDiv = doc.select("div.book").first();
			//TODO if book folder does not exists
			if (!Files.exists(bookFolderName)) {
				ImageIO.write(Book.getImageFromUrl(
						mainDiv.select("img").first().absUrl("src")),
						"jpg",
						new File(Paths.get(OUTPUT_DIR_IMAGES, bookFileName.replace(".txt", "") + ".jpg").toString()));
			}
			
			Elements ps = mainDiv.select("p");
			book.setLevel(ps.get(0).select("a").text().split(" ")[1]);
			book.setLevelLetter(ps.get(0).select("a").text().split(" ")[0]);
			book.setGenre(ps.get(1).select("a").text());
			book.setStoryline(ps.get(3).text());

			book.setHardwords(new ArrayList<String>(Arrays.asList(ps.get(5)
					.text().replace("Hard words: ", "")
					.split(", "))));
			
			String[] wordNumbers = doc.select("h3").get(1).text().split(" ");
			book.setUniqueWords(Integer.parseInt(wordNumbers[4].trim()));
			book.setTotalWords(Integer.parseInt(wordNumbers[7].trim()));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    
    private static ArrayList<String> getFileNamesFromFolder(final File folder) {
    	ArrayList<String> books = new ArrayList<String>();
    	try{
			if (folder.exists()){
				for (final File fileEntry : folder.listFiles()) {
					if (fileEntry.isFile()) {
						if (fileEntry.getPath().endsWith(".txt")) {
							books.add(fileEntry.getName());
						}
					}
				}
			}
		} catch (Exception e){

		}

    	return books;
    }
    
    public static ArrayList<String> getFileNamesFromFolder2(final File folder){
    	File[] files = folder.listFiles();
    	ArrayList<String> fileNames = new ArrayList<String>();
    	
    	for (File f: files) {
    		fileNames.add(f.toString());
    	}
    	return fileNames;
    }
    
    private static BufferedImage getBookCover(String fileName) {
    	return Book.getImageFromLocalFile(fileName);
    }
}
