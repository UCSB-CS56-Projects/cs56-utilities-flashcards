package edu.ucsb.cs56.W12.syeshanov.flashcardsim;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.util.ArrayList;


public class CreateDeckFrame extends JFrame { 
    public CreateDeckFrame () {
	super("Create a new deck");
	this.outer = this;
	
	JPanel contentPanel = new JPanel();
	this.setContentPane(contentPanel);
	contentPanel.setBorder(new EmptyBorder(10,20,50,20));


	BoxLayout layout = new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS);
	this.setLayout(layout);

	this.positionLabel = new JLabel("# of #");
	this.positionLabel.setHorizontalAlignment(SwingConstants.CENTER);
	this.positionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	
	this.add(this.positionLabel);

	
	this.cardTextPanel = new JPanel();
	this.cardTextPanel.setBorder(new LineBorder(Color.black));
	this.cardTextPanel.setPreferredSize(new Dimension(500,300));
	this.cardTextPanel.setMinimumSize(new Dimension(500,300));
	this.cardTextPanel.setBackground(Color.white);
	this.cardTextPanel.setLayout(new GridLayout(1,1));

	this.cardTextLabel = new JLabel();
	this.cardTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
	this.cardTextLabel.setBackground(Color.white);
	this.cardTextLabel.setFont(new Font("Courier New", 
					    Font.BOLD, 20));
	this.cardTextLabel.setText("Card Front Text");
	this.cardTextPanel.add(this.cardTextLabel);


	this.add(this.cardTextPanel);


	JPanel buttonPanel = new JPanel();

	this.flipCardButton = createToggleButton("Flip");
	this.flipCardButton.addActionListener(new FlipCardButtonListener());
	buttonPanel.add(this.flipCardButton);

	this.editCardButton = createButton("Edit");
	this.editCardButton.addActionListener(new EditCardButtonListener());
	buttonPanel.add(this.editCardButton);

	this.nextCardButton = createButton("Next Card");
	this.nextCardButton.addActionListener(new NextCardButtonListener());
	buttonPanel.add(this.nextCardButton);

	this.add(buttonPanel);
	

	this.newCardButton = createButton("New Card", 150);
	this.newCardButton.addActionListener(new NewCardButtonListener());
	
	this.saveButton = createButton("Save", 150);
	this.saveButton.addActionListener(new SaveButtonListener());


	JPanel newCardPanel = new JPanel();
	newCardPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0,0));
	newCardPanel.add(this.newCardButton);

	JPanel savePanel = new JPanel();
	savePanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0,0));
	savePanel.add(this.saveButton);
		

	buttonPanel = new JPanel();
	buttonPanel.setLayout(new GridLayout(1,2));
	buttonPanel.add(newCardPanel);
	buttonPanel.add(savePanel);
	buttonPanel.setBorder(new EmptyBorder(25, 0,0,0));
	this.add(buttonPanel);

	this.pack();
	
	this.setMinimumSize(new Dimension(this.getWidth(), this.getHeight()));
	this.newDeck = new Deck();
	this.currentCard = null;
	this.flippedFlag = false;
	this.showCard();
	this.actionListeners = new ArrayList<ActionListener>();
    }

    public void showCard() {
	String cardText = "Create a new card to begin.";
	if(currentCard != null) {
	    this.editCardButton.setEnabled(true);
	    if(this.flippedFlag)
		cardText = currentCard.getBackText();
	    else
		cardText = currentCard.getFrontText();
	}
	else {
	    this.editCardButton.setEnabled(false);
	}

	this.cardTextLabel.setText(cardText);
	int offset = 1;
	if(this.currentCard == null)
	    offset = 0;


	this.positionLabel.setText(String.format("%d of %d", this.cardNum + offset, 
						 this.newDeck.getSize() + offset));
    }

    public void addActionListener(ActionListener listener) {
	this.actionListeners.add(listener);
    }

    /**
       Helper method for constructing a button with a given label and the default width (125).
       @param label The label for the button.
     */
    private JButton createButton(String label) {
	return createButton(label, 125);
    }


    /**
       Helper method to construct a button with a specified width and label.
       @param label The label for the button.
       @param width The width of the button.
     */
    private JButton createButton(String label, int width) {
	JButton button = new JButton(label);
	button.setPreferredSize(new Dimension(width, 20));
	return button;	
    }


    /**
       Helper method for constructing a toggle button with a given label and the default width (125).
       @param label The label for the button.
     */

    private JToggleButton createToggleButton(String label) {
	return createToggleButton(label, 125);
    }

    /**
       Helper method for constructing a toggle button with a given label and width.
       @param label The label for the button.
       @param width The width for the button.
     */

    private JToggleButton createToggleButton(String label, int width) {
	JToggleButton button = new JToggleButton(label);
	button.setPreferredSize(new Dimension(width, 20));
	return button;
    }

    public Deck getDeck() {
	return this.newDeck;
    }

    public class NextCardButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent ev) {
	    if(currentCard != null)
		newDeck.putBack(currentCard);

	    currentCard = newDeck.draw();
	    if(newDeck.getSize() > 0)
		cardNum = (cardNum+1) % (newDeck.getSize()+1);
	    else if(currentCard != null)
		cardNum = 1;
	    else
		cardNum = 0;

	    flippedFlag = false;
	    outer.showCard();
	}
    }

    public class EditCardButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent ev) {
	    cardDialog = new NewCardDialog(outer);
	    cardDialog.addActionListener(new EditCardDialogListener());
	    cardDialog.pack();
	    cardDialog.setLocationRelativeTo(editCardButton);
	    cardDialog.setVisible(true);
	}
    }

    public class NewCardDialogListener implements ActionListener {
	public void actionPerformed(ActionEvent ev) {
	    if(currentCard != null)
		newDeck.putBack(currentCard);

	    String frontText = cardDialog.getFrontText();
	    String backText = cardDialog.getBackText();
	    FlashCard newCard = new FlashCard(frontText, backText);
	    currentCard = newCard;
	    cardDialog.setVisible(false);
	    outer.showCard();

	}	
    }

    public class EditCardDialogListener implements ActionListener {
	public void actionPerformed(ActionEvent ev) {
	    if(currentCard == null)
		return;

	    String frontText = cardDialog.getFrontText();
	    String backText = cardDialog.getBackText();
	    FlashCard newCard = new FlashCard(frontText, backText);
	    currentCard = newCard;
	    cardDialog.setVisible(false);
	    outer.showCard();
	}

    }

    public class FlipCardButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent ev) {
	    if(flipCardButton.isSelected())
		flippedFlag = true;
	    else
		flippedFlag = false;

	    outer.showCard();
	}
    }

    public class SaveButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent ev) {
	    JFileChooser chooser = new JFileChooser();
	    int option = chooser.showSaveDialog(outer);
	    try{
		if(option == JFileChooser.APPROVE_OPTION) {
		    String fileName = chooser.getSelectedFile().getCanonicalPath();
		    if(currentCard != null)
			newDeck.putBack(currentCard);
		    
		    ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(fileName));
		    outStream.writeObject(newDeck);
		    ev = new ActionEvent(outer, 0, "DeckCreated");
		    for(ActionListener listener: outer.actionListeners)
			listener.actionPerformed(ev);
		       
		}	
	    }
	    catch (Exception e) {
		JOptionPane.showMessageDialog(outer, "Error:" + e,
					      "Error saving file.",
					      JOptionPane.ERROR_MESSAGE );
		e.printStackTrace();
					      
	    }
	}
    }


    public class NewCardButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent ev) {
	    cardDialog = new NewCardDialog(outer);
	    cardDialog.addActionListener(new NewCardDialogListener());
	    cardDialog.pack();
	    cardDialog.setLocationRelativeTo(newCardButton);
	    cardDialog.setVisible(true);
	}
    }


    public static void main(String [] args ) {
	CreateDeckFrame frame = new CreateDeckFrame();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);
    }

    private ArrayList<ActionListener> actionListeners;
    private NewCardDialog cardDialog;
    private CreateDeckFrame outer;
    private int cardNum;
    private boolean flippedFlag;
    private Deck newDeck;
    private FlashCard currentCard;
    private JToggleButton flipCardButton;
    private JButton nextCardButton;
    private JButton newCardButton;
    private JButton editCardButton;
    private JButton saveButton;
    private JLabel positionLabel;
    private JLabel cardTextLabel;
    private JPanel cardTextPanel;


}