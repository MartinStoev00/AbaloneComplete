package ss.project.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ss.project.abalone.Board;
import ss.project.abalone.Mark;

class PlayerTest {

	Board b;

	@BeforeEach
	void setUp() throws Exception {
		b = new Board(2);
	}

	@Test
	void testMarbleContent() {
		for (int i = 1; i <= 5; i++) {
			assertEquals(Mark.WW, b.getMark(String.valueOf(i) + "A"));
		}
		for (int i = 1; i <= 6; i++) {
			assertEquals(Mark.WW, b.getMark(String.valueOf(i) + "B"));
		}
		
		for (int i = 3; i <= 5; i++) {
			assertEquals(Mark.WW, b.getMark(String.valueOf(i) + "C"));
		}
		for (int i = 5; i <= 9; i++) {
			assertEquals(Mark.BB, b.getMark(String.valueOf(i) + "I"));
		}
		for (int i = 4; i <= 9; i++) {
			assertEquals(Mark.BB, b.getMark(String.valueOf(i) + "H"));
		}
		
		for (int i = 5; i <= 7; i++) {
			assertEquals(Mark.BB, b.getMark(String.valueOf(i) + "G"));
		}
	}
	@Test
	void testMoveCorrect() {
		b = new Board(2);
		assertTrue(b.move("3C4C5C","1",Mark.WW));
		assertFalse(b.move("1B1A","6",Mark.WW));
		assertTrue(b.move("6B","1",Mark.WW));
		assertFalse(b.move("4H","5",Mark.WW));
		assertFalse(b.move("5B6B","1",Mark.WW));
		b = new Board(4);
		b.move("3B","1",Mark.YY);
		assertFalse(b.move("3E3D3C", "1", Mark.WW));
		b.move("7E8E", "6", Mark.BB);
		b.move("7E6E", "6", Mark.BB);
		b.move("3E","3",Mark.WW);
		assertFalse(b.move("4E5E6E", "6", Mark.BB));
		assertFalse(b.move("4E5E6E", "3", Mark.WW));
		assertTrue(b.move("4E5E6E", "6", Mark.WW));
		assertTrue(b.move("4E5E3E", "3", Mark.BB));
	}
	
	@Test 
	void pushingTest() {
		b = new Board(2);
		b.move("3A4B5C", "2", Mark.WW);
		b.move("6D4B5C", "2", Mark.WW);
		b.move("7E6D5C", "2", Mark.WW);
		b.move("6D7E8F", "1", Mark.WW);
		assertTrue(b.getMark("9H") == Mark.BB);
		assertTrue(b.move("6E7F8G", "2", Mark.WW));
		assertTrue(b.getMark("9H") == Mark.WW);
		System.out.println(b);
	}
}
