package cart;

import java.util.ArrayList;
import java.util.List;

import bookitem.Book;

public interface CartInterface {
	void printBookList(List<Book> booklist);

	boolean isCartInBook(String id);

	void insertBook(Book p);

	void removeCart(int numId);

	void deleteBook();
}