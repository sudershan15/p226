package gash.indexing;

public class Book {

	String title;
	String author;
	String release_date;
	String posting_date;
	String language;
	String translator;
	String produced_by;
	String content;
	String book_name;
	
	public String getBook_name() {
		return book_name;
	}

	public void setBook_name(String book_name) {
		this.book_name = book_name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Book() {
//		this.title = title;
//		this.author = author;
//		this.release_date = release_date;
//		this.posting_date = posting_date;
//		this.language = language;
//		this.translator = translator;
//		this.produced_by = produced_by;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getRelease_date() {
		return release_date;
	}

	public void setRelease_date(String release_date) {
		this.release_date = release_date;
	}

	public String getPosting_date() {
		return posting_date;
	}

	public void setPosting_date(String posting_date) {
		this.posting_date = posting_date;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTranslator() {
		return translator;
	}

	public void setTranslator(String translator) {
		this.translator = translator;
	}

	public String getProduced_by() {
		return produced_by;
	}

	public void setProduced_by(String produced_by) {
		this.produced_by = produced_by;
	}
	
	
}
