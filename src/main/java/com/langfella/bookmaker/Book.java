package com.langfella.bookmaker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Book {

	private String baseFileName;
	private String title;
	private String author;
	private String level;
	private String levelLetter;
	private String storyline;
	private String genre;
	private int uniqueWords;
	private int totalWords;
	private BufferedImage image;
	
	private ArrayList<String> chapters;
	private String excerpt;
	private ArrayList<String> hardwords;
	
	public String getSampleContent() {
		if (chapters.get(0).length() < 200){
			return chapters.get(0);
		} else {
			return chapters.get(0).substring(0, 200);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
			b.append("title: ").append(this.title).append("\t")
				.append("author: ").append(this.author).append("\t")
				.append("level: ").append(this.level).append("\t")
				.append("genre: ").append(this.genre).append("\t")
				.append("level letter: ").append(this.levelLetter).append("\t")
				.append("unique words: ").append(this.uniqueWords).append("\t")
				.append("total words: ").append(this.totalWords).append("\t");
		return b.toString();
	}
	
	static BufferedImage getImageFromUrl(String urlString) {
		URL url = null;
        
        BufferedImage image = null;
         
        try {
            url = new URL(urlString);
        } 
        catch (MalformedURLException e1){
            e1.printStackTrace();
        }
       
        try {
            image = ImageIO.read(url);             
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return image;
	}
	
	static BufferedImage getImageFromLocalFile(String filePath) {
		File file = new File(filePath);
        
        BufferedImage image = null;
         
        try {
            image = ImageIO.read(file);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return image;
	}


	public String getBaseFileName() {
		return baseFileName;
	}

	public void setBaseFileName(String baseFileName) {
		this.baseFileName = baseFileName;
	}

	public String getTitle() {
		return title;
	}

	void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	void setAuthor(String author) {
		this.author = author;
	}

	public String getLevel() {
		return level;
	}

	void setLevel(String level) {
		this.level = level;
	}

	public String getLevelLetter() {
		return levelLetter;
	}

	void setLevelLetter(String levelLetter) {
		this.levelLetter = levelLetter;
	}

	public String getStoryline() {
		return storyline;
	}

	void setStoryline(String storyline) {
		this.storyline = storyline;
	}

	public String getGenre() {
		return genre;
	}

	void setGenre(String genre) {
		this.genre = genre;
	}

	public int getUniqueWords() {
		return uniqueWords;
	}

	void setUniqueWords(int uniqueWords) {
		this.uniqueWords = uniqueWords;
	}

	public int getTotalWords() {
		return totalWords;
	}

	void setTotalWords(int totalWords) {
		this.totalWords = totalWords;
	}

	ArrayList<String> getChapters() {
		return chapters;
	}

	void setChapters(ArrayList<String> chapters) {
		this.chapters = chapters;
	}

	public String getExcerpt() {
		return this.excerpt;
	}

	void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}


	public ArrayList<String> getHardwords() {
		return hardwords;
	}

	void setHardwords(ArrayList<String> hardwords) {
		this.hardwords = hardwords;
	}

	
	
	
	
}


