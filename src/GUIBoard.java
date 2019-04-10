import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

public class GUIBoard extends Board {
	private GUI gui;
	private int rowSelected;
	private int colSelected;
	private int firstPositionSelected;
	private int secondPositionSelected;
	private List<Integer> possibleFinalLocations;
	private boolean moveMade;

	public GUIBoard(Player player1, Player player2) throws Exception {
		super(player1, player2);
		rowSelected = -1;
		colSelected = -1;
		firstPositionSelected = -1;
		secondPositionSelected = -1;
		moveMade = false;
		possibleFinalLocations = new LinkedList<Integer>();
		gui = new GUI();
		gui.setPreferredSize(new Dimension(500, 500));
		gui.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int mouseX = e.getX();
				int mouseY = e.getY();
				rowSelected = mouseY * getSize() / gui.getHeight();
				colSelected = mouseX * getSize() / gui.getWidth();
				if (getPiece(getPosition(rowSelected, colSelected)).getSide() == currentPlayer().getSide()) {
					firstPositionSelected = getPosition(rowSelected, colSelected);
					secondPositionSelected = -1;
					List<Move> availableMoves = getMovesForPiece(getPosition(rowSelected, colSelected));
					possibleFinalLocations = new LinkedList<Integer>();
					for (Move move : availableMoves) {
						possibleFinalLocations.add(move.getFinalPosition());
					}

				} else {
					secondPositionSelected = getPosition(rowSelected, colSelected);
				}
				if (firstPositionSelected != -1 && secondPositionSelected != -1
						&& isValidMove(new Move(firstPositionSelected, secondPositionSelected))) {
					moveMade = true;
				}
			}
		});
		gui.pack();
		gui.setVisible(true);
		gui.repaint();
	}

	@Override
	public void print() throws Exception {
		gui.repaint();
	}

	@Override
	public Move getHumanMove() throws Exception {
		while (firstPositionSelected == -1) {
			Thread.sleep(10);
		}
		while (!moveMade) {
			gui.repaint();
			Thread.sleep(10);
		}
		Move move = new Move(firstPositionSelected, secondPositionSelected);
		//debugging
		System.out.println(move.toString());
		if (isValidMove(move)) {
			moveMade = false;
			firstPositionSelected = -1;
			secondPositionSelected = -1;
			possibleFinalLocations = new LinkedList<Integer>();
			return move;
		} else {
			return getHumanMove();
		}
	}

	@Override
	public void makeMove(Move move) throws Exception {
		super.makeMove(move);
		gui.repaint();
	}

	@Override
	public void close() {
		gui.setVisible(false);
	}

	private class GUI extends JFrame {
		GridLayout gridLayout;

		public GUI() {
			super();
			gridLayout = new GridLayout(size, size);
			this.setLayout(gridLayout);
			for (int row = 0; row < size; row++) {
				for (int col = 0; col < size; col++) {
					add(new Cell(row, col));
				}
			}
		}

		private class Cell extends JComponent {
			int row;
			int column;
			Color backgroundColor;

			public Cell(int ROW, int COL) {
				super();
				row = ROW;
				column = COL;
				this.setPreferredSize(new Dimension(50, 50));
				backgroundColor = (row + column) % 2 == 0 ? Color.DARK_GRAY : Color.LIGHT_GRAY;
			}

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(backgroundColor);
				g.fillRect(0, 0, getWidth(), getHeight());
				if (!isEmpty(row, column)) {
					g.setColor(getOppositeColor(row, column));
					if (isKing(row, column)) {
						g.setColor(getColor(row, column));
						g.fillOval(0, 0, getWidth(), getHeight());
						g.setColor(getOppositeColor(row, column));
						g.drawOval(0, 0, getWidth(), getHeight());
						g.drawOval(5, 5, getWidth() - 10, getHeight() - 10);
						g.drawOval(10, 10, getWidth() - 20, getHeight() - 20);
					} else if (isQueen(row, column)) {
						g.setColor(getColor(row, column));
						g.fillOval(0, 0, getWidth(), getHeight());
						g.setColor(getOppositeColor(row, column));
						g.drawOval(10, 10, getWidth() - 20, getHeight() - 20);
					} else if (isRook(row, column)) {
						g.setColor(getColor(row, column));
						g.fillRect(10, 10, getWidth() - 20, getHeight() - 20);
					} else if (isBishop(row, column)) {
						g.setColor(getColor(row, column));
						g.fillOval(0, 0, getWidth(), getHeight());
						g.setColor(getOppositeColor(row, column));
						g.drawLine((int) (getWidth() * .15), (int) (getHeight() * .15), (int) (getWidth() * .85),
								(int) (getHeight() * .85));
						g.drawLine((int) (getWidth() * .15), (int) (getHeight() * .85), (int) (getWidth() * .85),
								(int) (getHeight() * .15));
					} else if (isKnight(row, column)) {
						g.setColor(getColor(row, column));
						g.fillOval(0, 0, getWidth(), getHeight());
						g.setColor(getOppositeColor(row, column));
						if (isBlack(row, column)) {
							g.drawLine((int) (getWidth() * .15), (int) (getHeight() * .15), (int) (getWidth() * .5),
									getHeight());
							g.drawLine((int) (getWidth() * .85), (int) (getHeight() * .15), (int) (getWidth() * .5),
									getHeight());
							g.drawLine((int) (getWidth() * .15), (int) (getHeight() * .15), (int) (getWidth() * .85),
									(int) (getHeight() * .15));
						} else if(isWhite(row,column)){
							g.drawLine((int) (getWidth() * .15), (int) (getHeight() * .85), (int) (getWidth() * .5),
									0);
							g.drawLine((int) (getWidth() * .85), (int) (getHeight() * .85), (int) (getWidth() * .5),
									0);
							g.drawLine((int) (getWidth() * .15), (int) (getHeight() * .85), (int) (getWidth() * .85),
									(int) (getHeight() * .85));
						}
					} else if (isPawn(row, column)) {
						g.setColor(getColor(row, column));
						g.fillOval(0, 0, getWidth(), getHeight());
					}
				}

				if (firstPositionSelected == getPosition(row, column)) {
					g.setColor(Color.yellow);
					g.fillOval(getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2);
				}
				if (possibleFinalLocations.contains(getPosition(row, column))) {
					g.setColor(Color.CYAN);
					g.fillOval(getWidth() / 4, getHeight() / 2, getWidth() / 2, getHeight() / 2);
				}

			}

			private Color getColor(int row, int column) {
				if (isBlack(row, column)) {
					return Color.BLACK;
				} else if (isWhite(row, column)) {
					return Color.WHITE;
				} else {
					return Color.BLUE;
				}
			}

			private Color getOppositeColor(int row, int column) {
				if (isBlack(row, column)) {
					return Color.WHITE;
				} else if (isWhite(row, column)) {
					return Color.BLACK;
				} else {
					return Color.BLUE;
				}
			}
		}
	}
}
