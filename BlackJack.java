import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements https://www.youtube.com/watch?v=xjqTIzYkGdI except splitting (which seems like 2 players).
 * Minor deviations:
 * 1. No split
 * 2. If table has an Ace, it is always counted as 11, while drawing card
 * 3. When it is table's turn, table must hit with a rank 16 or less
 */
public class BlackJack extends JFrame implements ActionListener {

    // 4 aces, 4 2's, and 3 3's -> 11 cards, rank = 4 + 8 + 9 = 21
    final static int MAX_CARDS = 11;

    // at lower action panel
    final JButton btnDeal = new JButton("Deal");
    final JButton btnInsurance = new JButton("Buy Insurance");
    final JButton btnSurrender = new JButton("Surrender");
    final JButton btnProceed = new JButton("Proceed");
    final JButton btnDoubleDown = new JButton("Double Down");
    final JButton btnHit = new JButton("Hit");
    final JButton btnStay = new JButton("Stay");
    final JButton btnRestart = new JButton("Restart");
    final JButton btnExit = new JButton("Exit");

    // on the left, show the deck(s)
    final JLabel lblServingDeck = new JLabel();
    final JLabel lblServingDeckSize = new JLabel("Serving Deck: 52 Cards");
    final JLabel lblBackupDeck = new JLabel();
    final JLabel lblBackupDeckSize = new JLabel("Backup Deck: 0 Cards");

    // on the right, show earning and bidding
    final JLabel lblTableEarning = new JLabel("Table Earning: 0");
    final JLabel lblBid = new JLabel("Bid: ");
    final JTextField txtBidAmount = new JTextField("100");
    final JLabel lblPlayerEarning = new JLabel("Player Earning: 0");

    // the main game table
    final JLabel lblTableCard[] = new JLabel[MAX_CARDS];
    final JLabel lblPlayerCard[] = new JLabel[MAX_CARDS];
    final JLabel lblTableRank = new JLabel("Rank: ");
    final JLabel lblPlayerRank = new JLabel("Rank: ");
    final JLabel lblStatus = new JLabel("");

    // a raised border
    final Border compoundBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createLoweredBevelBorder());

    // couple of images
    final ImageIcon cardBackIcon = new ImageIcon(getClass().getResource("images/back.png"));
    final ImageIcon winnerIcon = new ImageIcon(getClass().getResource("images/winner.jpeg"));
    final ImageIcon noWinnerIcon = new ImageIcon(getClass().getResource("images/no_winner.jpg"));
    final ImageIcon looserIcon = new ImageIcon(getClass().getResource("images/looser.jpg"));
    final ImageIcon dealIcon = new ImageIcon(getClass().getResource("images/deal.png"));
    final ImageIcon gameOnIcon = new ImageIcon(getClass().getResource("images/game_on_going.jpg"));
    final ImageIcon insuredIcon = new ImageIcon(getClass().getResource("images/insured.png"));
    final ImageIcon surrenderIcon = new ImageIcon(getClass().getResource("images/surrendered.png"));
    final ImageIcon pickACardIcon = new ImageIcon(getClass().getResource("images/pick_a_card.png"));
    final ImageIcon blackJackIcon = new ImageIcon(getClass().getResource("images/black_jack.png"));

    // game objects....
    // with UI, there is no sequential flow, every state needs to maintened
    final Deck returnCardDeck = new Deck(); // returned card deck
    final Deck servingCardDeck = new Deck(false, returnCardDeck); // the serving deck

    final List<Card> tableCards = new ArrayList<>(); // cards table has
    final List<Card> playerCards = new ArrayList<>(); // cards player has

    int currentBid; // current bid
    int tableEarning; // total earning for table
    int playerEarning; // total earning for player
    boolean purchasedInsurance; // did player earned insurance?

    int tableRank; // table's total rank
    int playerRank; // player's total rank
    boolean tableHasAce; // does the table has an ace
    boolean playerHasAce; // does the player has an ace

    public BlackJack() {
        // Prepare the UI
        setTitle("BlackJack");
        setSize(1536, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JPanel pnlMain = new JPanel(); // Main Window
        pnlMain.setLayout(new BorderLayout(5, 5));

        final JPanel pnlActions = new JPanel();
        pnlActions.setLayout(new FlowLayout());
        pnlActions.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        pnlActions.setBorder(compoundBorder);
        pnlActions.add(btnDeal);
        pnlActions.add(btnInsurance);
        pnlActions.add(btnSurrender);
        pnlActions.add(btnProceed);
        pnlActions.add(btnDoubleDown);
        pnlActions.add(btnHit);
        pnlActions.add(btnStay);
        pnlActions.add(btnRestart);
        pnlActions.add(btnExit);

        btnDeal.addActionListener(this);
        btnInsurance.addActionListener(this);
        btnSurrender.addActionListener(this);
        btnProceed.addActionListener(this);
        btnDoubleDown.addActionListener(this);
        btnHit.addActionListener(this);
        btnStay.addActionListener(this);
        btnRestart.addActionListener(this);
        btnExit.addActionListener(this);

        pnlMain.add(pnlActions, BorderLayout.PAGE_END);

        final JPanel pnlDecks = new JPanel();
        pnlDecks.setLayout(new GridLayout(0, 1));
        pnlDecks.setBorder(compoundBorder);
        pnlDecks.add(lblServingDeck);
        pnlDecks.add(lblServingDeckSize);
        pnlDecks.add(lblBackupDeck);
        pnlDecks.add(lblBackupDeckSize);

        pnlMain.add(pnlDecks, BorderLayout.LINE_START);

        final JPanel pnlBid = new JPanel();
        pnlBid.setLayout(new GridLayout(0, 1));
        pnlBid.add(lblBid);
        pnlBid.add(txtBidAmount);

        final JPanel pnlEarning = new JPanel();
        pnlEarning.setLayout(new GridLayout(0, 1));
        pnlEarning.setBorder(compoundBorder);
        pnlEarning.add(lblTableEarning);
        pnlEarning.add(pnlBid);
        pnlEarning.add(lblPlayerEarning);

        pnlMain.add(pnlEarning, BorderLayout.LINE_END);

        final JPanel pnlTableCards = new JPanel();
        pnlTableCards.setLayout(new GridLayout(1, 0));
        pnlTableCards.setBorder(BorderFactory.createRaisedBevelBorder());
        final JPanel pnlPlayerCards = new JPanel();
        pnlPlayerCards.setLayout(new GridLayout(1, 0));
        pnlPlayerCards.setBorder(compoundBorder);
        for (int i = 0; i < MAX_CARDS; i++) {
            lblTableCard[i] = new JLabel();
            lblPlayerCard[i] = new JLabel();

            lblTableCard[i].setBorder(BorderFactory.createRaisedBevelBorder());
            lblPlayerCard[i].setBorder(BorderFactory.createRaisedBevelBorder());

            lblTableCard[i].setHorizontalAlignment(SwingConstants.CENTER);
            lblTableCard[i].setVerticalAlignment(SwingConstants.CENTER);
            lblPlayerCard[i].setHorizontalAlignment(SwingConstants.CENTER);
            lblPlayerCard[i].setVerticalAlignment(SwingConstants.CENTER);

            pnlTableCards.add(lblTableCard[i]);
            pnlPlayerCards.add(lblPlayerCard[i]);
        }
        pnlTableCards.add(lblTableRank);
        pnlPlayerCards.add(lblPlayerRank);

        final JPanel pnlGame = new JPanel();
        pnlGame.setLayout(new GridLayout(0, 1));
        pnlGame.setBorder(compoundBorder);
        pnlGame.add(pnlTableCards);
        pnlGame.add(lblStatus);
        pnlGame.add(pnlPlayerCards);
        lblStatus.setVerticalAlignment(SwingConstants.CENTER);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);

        pnlMain.add(pnlGame, BorderLayout.CENTER);

        setContentPane(pnlMain);
        setVisible(true);

        // shuffle the serving deck
        servingCardDeck.shuffle();

        // set up the "deal" scenario
        setDealState();
    }

    private static void fitImageToLabel(final JLabel label, final ImageIcon icon) {
        final ImageIcon resizedIcon = new ImageIcon(icon.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_DEFAULT));
        label.setIcon(resizedIcon);
        label.setText(null);
    }

    private void setDealState() {
        currentBid = 0;
        txtBidAmount.setText("100"); // default bid is 100
        txtBidAmount.setEnabled(true);

        // except exit and deal button, disable all other buttons
        setActionPanelState(true);

        // show that "deal" image
        fitImageToLabel(lblStatus, dealIcon);

        // set all card places on the table with "pick a card" image
        for (int i = 0; i < MAX_CARDS; i++) {
            fitImageToLabel(lblTableCard[i], pickACardIcon);
            fitImageToLabel(lblPlayerCard[i], pickACardIcon);
        }

        // game has not started -> no insurance, no ace dealt, ranks are 0
        purchasedInsurance = false;
        tableHasAce = playerHasAce = false;
        playerRank = tableRank = 0;

        // draw card (left), earning (right), empty ranks
        drawCardPanel();
        drawEarningPanel();
        lblTableRank.setText("Rank: ");
        lblPlayerRank.setText("Rank: ");
    }

    // when user clicks "Deal" button
    private void handlePostDealState() {
        final String bidAmount = txtBidAmount.getText();

        try {
            // first make sure that the bid is valid
            currentBid = Integer.valueOf(bidAmount);
            if (currentBid == 0) {
                throw new NumberFormatException("0 bid is not allowed!");
            } else if (currentBid < 0) {
                throw new NumberFormatException("Negative bid is not allowed!");
            } else if (currentBid % 2 != 0) {
                throw new NumberFormatException("Bid must be even!");
            }

            // disable the bid area, so that it cannot be changed throughout the game
            txtBidAmount.setEnabled(false);

            // deal 2 cards
            for (int i = 0; i < 2; i++) {
                // add to player
                final Card playerCard = servingCardDeck.take();
                fitImageToLabel(lblPlayerCard[i], playerCard.getIcon());
                playerCards.add(playerCard);
                if (playerCard.getRank().equals(Rank.Ace)) {
                    playerHasAce = true;
                }
                playerRank += playerCard.getRank().getNumericValue();

                // add to table
                final Card tableCard = servingCardDeck.take();
                if (i == 0) {
                    fitImageToLabel(lblTableCard[i], tableCard.getIcon());
                } else {
                    fitImageToLabel(lblTableCard[i], cardBackIcon);
                }
                tableCards.add(tableCard);
                if (tableCard.getRank().equals(Rank.Ace)) {
                    tableHasAce = true;
                }
                tableRank += tableCard.getRank().getNumericValue();
            }

            final int playerScore = computePlayerScore();
            if (playerScore == 21) {
                final int tableScore = computeTableScore();
                if (tableScore == 21) {
                    // no winner
                    fitImageToLabel(lblStatus, noWinnerIcon);
                } else {
                    // black jack
                    // twice the earning
                    fitImageToLabel(lblStatus, blackJackIcon);
                    tableEarning -= 2 * currentBid;
                    playerEarning += 2 * currentBid;
                }
                // draw player and table ranks
                drawPlayerRank();
                fitImageToLabel(lblTableCard[tableCards.size() - 1],
                        tableCards.get(tableCards.size() - 1).getIcon());
                drawTableRank();

                // update earning
                drawEarningPanel();
                // setup scene to restart game
                setActionPanelStateToRestart();
            } else {
                // turn on insurance, surrender, and proceed
                // turn off deal (as deal is complete)
                setActionPanelState(false);

                // draw player's rank, but not table's
                drawPlayerRank();

                // update card panel
                drawCardPanel();
                //drawEarningPanel();

                // game is on...
                fitImageToLabel(lblStatus, gameOnIcon);
            }
        } catch (final Exception e) {
            // if the bid is not valid, show an error message and do nothing
            JOptionPane.showMessageDialog(this, e.getMessage(), "Invalid Bid", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // player wants to purchase insurance
    private void handleInsurance() {
        // mark that insurance has been purchased
        purchasedInsurance = true;
        txtBidAmount.setText(Integer.toString(currentBid) + " (Insured)");

        // with insurance, the bid essentially doubles
        // insurance is beneficial for player is table has 21
        // and then table takes 1/2 and player keeps 1/2
        // otherwise, the game continues, with double the bid
        final int tableScore = computeTableScore();
        if (tableScore == 21) {
            // show both player's and table's ranks
            drawPlayerRank();
            fitImageToLabel(lblTableCard[tableCards.size() - 1],
                    tableCards.get(tableCards.size() - 1).getIcon());
            drawTableRank();

            fitImageToLabel(lblStatus, insuredIcon);
            tableEarning += currentBid;
            playerEarning -= currentBid;

            // update earning
            drawEarningPanel();
            // setup scene to restart game
            setActionPanelStateToRestart();
        } else {
            // setup scene to main game
            setActionPanelStateToMainGame();
        }
    }

    // player wants to surrender
    private void handleSurrender() {
        // doesn't matter about the ranks, half of the bid
        // goes to the table and half player keeps
        // the game ends, and player can restart
        drawPlayerRank();
        fitImageToLabel(lblTableCard[tableCards.size() - 1],
                tableCards.get(tableCards.size() - 1).getIcon());
        fitImageToLabel(lblStatus, surrenderIcon);
        drawTableRank();

        tableEarning += currentBid / 2;
        playerEarning -= currentBid / 2;

        setActionPanelStateToRestart();
        drawEarningPanel();
    }

    // player wants to proceed to main game
    // without insurance or surrender
    private void handleProceed() {
        // table checks it score
        // if the score is 21, game ends there
        // otherwise, the game continues
        final int tableScore = computeTableScore();
        if (tableScore == 21) {
            drawPlayerRank();
            fitImageToLabel(lblTableCard[tableCards.size() - 1],
                    tableCards.get(tableCards.size() - 1).getIcon());
            drawTableRank();
            determineWinner();

            setActionPanelStateToRestart();
            drawEarningPanel();
        } else {
            setActionPanelStateToMainGame();
        }
    }

    // player wants to double down
    private void handleDoubleDown() {
        // bid doubles
        currentBid *= 2;
        txtBidAmount.setText(Integer.toString(currentBid) + (purchasedInsurance ? " (Insured)" : ""));

        // one card is served to player
        final Card playerCard = servingCardDeck.take();
        fitImageToLabel(lblPlayerCard[playerCards.size()], playerCard.getIcon());
        playerCards.add(playerCard);
        if (playerCard.getRank().equals(Rank.Ace)) {
            playerHasAce = true;
        }
        playerRank += playerCard.getRank().getNumericValue();

        // redraw panels, determine winner based on the ranks
        drawCardPanel();
        drawPlayerRank();
        fitImageToLabel(lblTableCard[tableCards.size() - 1],
                tableCards.get(tableCards.size() - 1).getIcon());
        drawTableRank();
        determineWinner();

        setActionPanelStateToRestart();
        drawEarningPanel();
    }

    // player wants one card (Hit)
    private void handleHit() {
        // player takes 1 card
        final Card playerCard = servingCardDeck.take();
        fitImageToLabel(lblPlayerCard[playerCards.size()], playerCard.getIcon());
        playerCards.add(playerCard);
        if (playerCard.getRank().equals(Rank.Ace)) {
            playerHasAce = true;
        }
        playerRank += playerCard.getRank().getNumericValue();

        drawCardPanel();
        drawPlayerRank();
        btnDoubleDown.setEnabled(false);

        // if player goes over 21, please is the loser
        if (playerRank > 21) {
            fitImageToLabel(lblTableCard[tableCards.size() - 1],
                    tableCards.get(tableCards.size() - 1).getIcon());
            drawTableRank();
            determineWinner();

            setActionPanelStateToRestart();
            drawEarningPanel();
        }
    }

    // player wants to stay
    private void handleStay() {
        // then it is table's turn (to hit or stay, through the logic is different)
        // a winner/loose/... is decided
        // and game ends there
        tableTurn();
        drawCardPanel();
        drawPlayerRank();
        drawTableRank();
        determineWinner();
        setActionPanelStateToRestart();
        drawEarningPanel();
    }

    // logic to handle table's turn
    private void tableTurn() {
        fitImageToLabel(lblTableCard[tableCards.size() - 1],
                tableCards.get(tableCards.size() - 1).getIcon());

        // if table's rank is less than or 16, it continues to draw a card
        // otherwise stays
        // for this assume ace is 11
        // finally, a winner/loser is decided and game ends
        int tableRankFinal = tableRank + (tableHasAce ? 10 : 0);
        for (int i = 2  ; i < MAX_CARDS && tableRankFinal <= 16; i++) {
            final Card tableCard = servingCardDeck.take();
            fitImageToLabel(lblTableCard[i], tableCard.getIcon());
            tableCards.add(tableCard);

            if (!tableHasAce && tableCard.getRank().equals(Rank.Ace)) {
                tableHasAce = true;
                tableRankFinal += 10;
            }
            tableRank += tableCard.getRank().getNumericValue();
            tableRankFinal += tableCard.getRank().getNumericValue();
        }
    }

    // determines winner or loser
    private void determineWinner() {
        final int playerScore = computePlayerScore();
        final int tableScore = computeTableScore();

        // bid is double, if insurance was purchased
        final int bid = currentBid * (purchasedInsurance ? 2 : 1);

        if (playerScore > 21) {
            // if player score is > 21, doesn't matter what is the
            // score of the table, player loses
            tableEarning += bid;
            playerEarning -= bid;
            lblTableEarning.setText("Table Earning: " + tableEarning);
            fitImageToLabel(lblStatus, looserIcon);
        } else if (tableScore > 21) {
            // else, if table went overboard, player is the winner
            tableEarning -= bid;
            playerEarning += bid;
            lblPlayerEarning.setText("Player Earning: " + playerEarning);
            fitImageToLabel(lblStatus, winnerIcon);
        } else if (playerScore == tableScore) {
            // both are less than 21, but same score, no winner
            fitImageToLabel(lblStatus, noWinnerIcon);
        } else if (playerScore > tableScore) {
            // player has the higher score, player wins winner
            tableEarning -= bid;
            playerEarning += bid;
            lblPlayerEarning.setText("Player Earning: " + playerEarning);
            fitImageToLabel(lblStatus, winnerIcon);
        } else { // if (tableScore > playerScore) ...
            // table has higher score, so player is the loser
            tableEarning += bid;
            playerEarning -= bid;
            lblTableEarning.setText("Table Earning: " + tableEarning);
            fitImageToLabel(lblStatus, looserIcon);
        }
    }

    private int computePlayerScore() {
        if (playerHasAce) {
            return (playerRank + 10) > 21 ? playerRank : playerRank + 10;
        } else {
            return playerRank;
        }
    }

    private int computeTableScore() {
        if (tableHasAce) {
            return  (tableRank + 10) > 21 ? tableRank : tableRank + 10;
        } else {
            return tableRank;
        }
    }

    // player wants to start a new game
    private void handleRestart() {
        // return player and table's card return deck
        returnCardDeck.addCards(playerCards);
        returnCardDeck.addCards(tableCards);
        playerCards.clear();
        tableCards.clear();

        // and setup the start scene
        setDealState();
    }

    private void setActionPanelStateToMainGame() {
        btnInsurance.setEnabled(false);
        btnSurrender.setEnabled(false);
        btnProceed.setEnabled(false);
        btnDoubleDown.setEnabled(true);
        btnHit.setEnabled(true);
        btnStay.setEnabled(true);
    }

    private void setActionPanelStateToRestart() {
        btnDeal.setEnabled(false);
        btnInsurance.setEnabled(false);
        btnSurrender.setEnabled(false);
        btnProceed.setEnabled(false);
        btnDoubleDown.setEnabled(false);
        btnHit.setEnabled(false);
        btnStay.setEnabled(false);
        btnRestart.setEnabled(true);
    }

    private void setActionPanelState(final boolean deal) {
        btnDeal.setEnabled(deal);
        btnInsurance.setEnabled(!deal);
        btnSurrender.setEnabled(!deal);
        btnProceed.setEnabled(!deal);
        btnDoubleDown.setEnabled(false);
        btnHit.setEnabled(false);
        btnStay.setEnabled(false);
        btnRestart.setEnabled(false);
    }

    private void drawCardPanel() {
        lblServingDeckSize.setText("Serving Deck: " + servingCardDeck.size() + " Cards");
        lblBackupDeckSize.setText("Backup Deck: " + returnCardDeck.size() + " Cards");

        if (servingCardDeck.size() > 0) {
            fitImageToLabel(lblServingDeck, cardBackIcon);
        } else {
            lblServingDeck.setIcon(null);
        }
        if (returnCardDeck.size() > 0) {
            fitImageToLabel(lblBackupDeck, returnCardDeck.peak().getIcon());
        } else {
            lblBackupDeck.setIcon(null);
        }
    }

    private void drawEarningPanel() {
        lblPlayerEarning.setText("Player Earning: " + playerEarning);
        lblTableEarning.setText("Table Earning: " + tableEarning);
    }

    private void drawPlayerRank() {
        final String rank;
        if (playerHasAce) {
            if ((playerRank + 10) > 21) {
                rank = "" + playerRank;
            } else {
                rank = "" + playerRank + " OR " + (playerRank + 10);
            }
        } else {
            rank = "" + playerRank;
        }

        lblPlayerRank.setText("Rank: " + rank);
    }

    private void drawTableRank() {
        final String rank;
        if (tableHasAce) {
            if ((tableRank + 10) > 21) {
                rank = "" + (tableRank + 10);
            } else {
                rank = "" + playerRank + " OR " + (playerRank + 10);
            }
        } else {
            rank = "" + tableRank;
        }

        lblTableRank.setText("Rank: " + rank);
    }

    // action handlers
    @Override
    public void actionPerformed(ActionEvent e) {
        final Object source = e.getSource();
        if (source == btnDeal) {
            handlePostDealState();
        } else if (source == btnInsurance) {
            handleInsurance();
        } else if (source == btnSurrender) {
            handleSurrender();
        } else if (source == btnProceed) {
            handleProceed();
        } else if (source == btnDoubleDown) {
            handleDoubleDown();
        } else if (source == btnHit) {
            handleHit();
        } else if (source == btnStay) {
            handleStay();
        } else if (source == btnRestart) {
            handleRestart();
        } else if (source == btnExit) {
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    public static void main(String[] args) {
        new BlackJack();
    }
}
