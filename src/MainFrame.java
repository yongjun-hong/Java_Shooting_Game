import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.awt.event.KeyListener;
import java.io.File;
import java.sql.Time;
import java.util.TimerTask;


public class MainFrame extends JFrame implements ActionListener {
	int pilot = -1;
	int level_,enemy_attacks;
	String name;
	private Image player;
	private Image gameScreen;
	private int playerWidth;
	private int playerHeight;
	public static final int SCREEN_WIDTH = 1200;
	public static final int SCREEN_HEIGHT = 720;
	public JTextField name_field;
	public JButton button;
	public JLabel label;
	public JLabel name_label;
	public JLabel level_label;
	public JPanel name_panel;

	public MainFrame()
	{
		// JFrame에 들어갈 것들
		name_panel = new JPanel();
		name_field = new JTextField(30);
		button = new JButton("입력");
		label = new JLabel("영문이름을 입력하시오: "); // 한글로 입력하면 게임을 플레이 할때 wasd가 안눌려지고 한글 ㅁㄴㅇㅈ으로 인식을 해서 영문/한글키를 눌러야한다.
		name_label = new JLabel();
		name_panel.add(label);
		name_panel.add(name_field);
		name_field.addActionListener(this);
		button.addActionListener(this);

		String[] levels = { "easy", "average", "hard" };
		level_label = new JLabel();
		JComboBox levellist = new JComboBox(levels);
		levellist.setSelectedIndex(0);
		levellist.addActionListener(new MyListener());


		//JFrame 설정하는 곳
		this.setName("이름");
		this.setLayout(new GridLayout(0,1));
		this.add(name_panel);
		this.add(levellist);
		this.add(name_label);
		this.add(level_label);
		this.add(button);
		this.setSize(500, 200);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.pack();

		ShootingGame g = new ShootingGame();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == name_field ||e.getSource() == button) // 입력을 했을때 프레임이 종료되게 하고싶다.
		{
			name = name_field.getText();
			name_label.setText("Good Luck "+name + "!"); 
		}
	}

	public static void main(String[] args) {
		new MainFrame();
	}

	private class MyListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox cb = (JComboBox) e.getSource();
			String name = (String) cb.getSelectedItem();
			if(name == "easy")
			{
				level_ = 1;
				level_label.setText("You have chosen an easy level.");
			}
			else if(name == "average")
			{
				level_ = 2;
				level_label.setText("You have chosen an average level.");
			}
			else if(name == "hard")
			{
				level_ = 3;
				level_label.setText("You have chosen an hard level.");
			}
		}
	}
	class ShootingGame extends JFrame {

		private Image bufferImage;
		private Graphics screenGraphic;

		private Image mainScreen = new ImageIcon("topgun_maverick.jpg").getImage(); 
		
		private Image easy_load = new ImageIcon("easy_loading_screen.png").getImage();
		private Image average_load = new ImageIcon("average_loading_screen.png").getImage();
		private Image hard_load = new ImageIcon("hard_loading_screen.png").getImage();

		private Image isLoadingScreen_2 = new ImageIcon("mav_ice.jpg").getImage();


		private boolean isMainScreen, isGameScreen, mav_ice, easy_loading,average_loading,hard_loading;


		private Game game = new Game();



		public ShootingGame() { 
			setTitle("Top Gun - Maverick"); 
			setUndecorated(true); // 프레임 타이틀바 없애기
			setSize(MainFrame.SCREEN_WIDTH,MainFrame.SCREEN_HEIGHT); 
			//setResizable(false);  // 프레임의 크기를 사용자가 조절할수 있게 할지 말지를 정한다.
			setLocationRelativeTo(null);  //* 프레임의 위치를 컴포넌트에 따라 상대적인 위치를 정한다. 매개변수에 null을 넣으면 화면의 정중앙에 프레임이 위치한다.
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
			setLayout(null); // 배치관리자를 지정하지 않는다는 의미로 절대 위치로 컴포넌트들을 나타냅니다.

			init();
		}

		private void init() {
			isMainScreen = true;
			easy_loading = false;
			average_loading = false;
			hard_loading = false;
			isGameScreen = false;
			mav_ice = false;

			addKeyListener(new keyListener());
		}


		private void gameStart() {
			isMainScreen = false;
			if(level_ == 1)
			{
				easy_loading = true;
				average_loading = false;
				hard_loading = false;
			} 
			else if(level_ == 2)
			{
				average_loading = true;
				easy_loading = false;
				hard_loading = false;
			}
			else if(level_ == 3)
			{
				hard_loading = true;
				easy_loading = false;
				average_loading = false;
			}
			mav_ice = false;

			Timer loadingTimer = new Timer();
			TimerTask loadingTask = new TimerTask() { // timer 클래스가 수행되야할 내용을 작성하는 클래스입니다
				@Override
				public void run() { // schedule에 의해서 2초 뒤에 run메소드가 실행된다.

					easy_loading = false;
					average_loading = false;
					hard_loading = false;
					isGameScreen = true; 
					mav_ice = false;
					game.start(); 
				}
			};
			loadingTimer.schedule(loadingTask,3000); // 지정한 시간에 지정한 작업을 시작한다.
		}


		public void paint(Graphics g) {
			bufferImage = createImage(MainFrame.SCREEN_WIDTH,MainFrame.SCREEN_HEIGHT);
			screenGraphic = bufferImage.getGraphics();
			screenDraw(screenGraphic);
			g.drawImage(bufferImage, 0,0,null);
		}

		public void screenDraw(Graphics g) {
			if (isMainScreen) {
				g.drawImage(mainScreen, 0, 0, null);
			}
			if(easy_loading)
			{
				g.drawImage(easy_load,0,0,null);
			}
			if(average_loading)
			{
				g.drawImage(average_load,0,0,null);
			}
			if(hard_loading)
			{
				g.drawImage(hard_load,0,0,null);
			}
			if(mav_ice) {
				g.drawImage(isLoadingScreen_2, 0, 0, null);
			}
			if (isGameScreen) { 
				g.drawImage(gameScreen, 0, 0, null);
				game.gameDraw(g);
			}
			this.repaint();
		}


		class keyListener extends KeyAdapter { // *

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {

				case KeyEvent.VK_W:
					game.setUp(true);
					break;
				case KeyEvent.VK_S:
					game.setDown(true);
					break;
				case KeyEvent.VK_A:
					game.setLeft(true);
					break;
				case KeyEvent.VK_D:
					game.setRight(true);
					break;


				case KeyEvent.VK_SPACE:
					game.setShooting(true);
					break;

				case KeyEvent.VK_ENTER:
					if (isMainScreen) {
						easy_loading = false;
						average_loading = false;
						hard_loading = false;
						isGameScreen = false; 
						mav_ice = true;
						break;
					}
				case KeyEvent.VK_1:
					if (isMainScreen) {
						pilot = 1;
						gameStart();
					}
				case KeyEvent.VK_2:
					if (isMainScreen) {
						pilot = 2;
						gameStart();
					}
					break;
				case KeyEvent.VK_ESCAPE:   
					System.exit(0);
					break;
				}
				if(pilot == 1)
				{
					gameScreen = new ImageIcon("game_screen_2.jpg").getImage();
					player = new ImageIcon("maverick's_jets.png").getImage();
					playerWidth = player.getWidth(null);
					playerHeight = player.getHeight(null);
				}
				else if(pilot == 2)
				{
					gameScreen = new ImageIcon("game_screen.jpg").getImage();
					player = new ImageIcon("iceman's_jets.png").getImage();
					playerWidth = player.getWidth(null);
					playerHeight = player.getHeight(null);
				}
			}


			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {

				case KeyEvent.VK_W:
					game.setUp(false);
					break;
				case KeyEvent.VK_S:
					game.setDown(false);
					break;
				case KeyEvent.VK_A:
					game.setLeft(false);
					break;
				case KeyEvent.VK_D:
					game.setRight(false);
					break;

				case KeyEvent.VK_SPACE:
					game.setShooting(false);
					break;

				}
			}

		}


	}
	class PlayerAttack {
		Image image = new ImageIcon("player_attack.png").getImage(); 
		int x,y; //

		int width = image.getWidth(null);
		int height = image.getHeight(null);
		int player_attack = 5; 


		public PlayerAttack(int x, int y) {
			this.x = x;
			this.y = y;
		}


		public void fire() {
			this.x += 15; 
		}

	}
	class Game extends Thread{ // run 메소드를 꼭 구현해야 한다. game.start를 구현하면 run메소드가 실행된다.

		private int delay = 20;
		private long pretime;
		private int cnt;        
		private int score;      
		Image image = null; // 적기의 초기값
		Image enemy_missiles = null; // 적기 공격의 초기값
		// 여기에 if문을 사용해서 조종사를 몇명 더 추가한다. 


		private int playerX, playerY;


		private int playerSpeed = 10; 
		private int playerHp = 30;

		private boolean up, down, left, right, shooting ; 
		private boolean isOver;

		private ArrayList<PlayerAttack> playerAttackList = new ArrayList<PlayerAttack>(); 
		private ArrayList<Enemy> enemyList = new ArrayList<Enemy>(); 
		private ArrayList<EnemyAttack> enemyAttackList = new ArrayList<EnemyAttack>();

		private PlayerAttack playerAttack;
		private Enemy enemy;
		private EnemyAttack enemyAttack;




		@Override
		public void run() {


			while (true) { 
				while (!isOver) { 
					pretime = System.currentTimeMillis(); //  // 1970년 1월 1일부터 지금까지의 시간을 밀리세컨드 (1/1000)초의 값을 반환한다.
					if (System.currentTimeMillis() - pretime < delay) {// delay = 20이다. 
						try {
							Thread.sleep(delay - System.currentTimeMillis() + pretime); // thread.sleep은 얼마동안 일시정지 상태로 있을것인지 밀리세컨드 단위로 시간을 알려주면 된다. ex) 1000은 1초 이다.
							keyProcess();
							playerAttackProcess();
							enemyAppearProcess();
							enemyMoveProcess();
							enemyAttackProcess();
							cnt++;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				try {
					Thread.sleep(100); 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}


		private void keyProcess(){

			if (up && playerY - playerSpeed > 0) playerY -= playerSpeed;
			if (down && playerY + playerHeight + playerSpeed < MainFrame.SCREEN_HEIGHT) playerY += playerSpeed;
			if (left && playerX - playerSpeed > 0) playerX -= playerSpeed;
			if (right && playerX + playerWidth + playerSpeed < MainFrame.SCREEN_WIDTH) playerX += playerSpeed;

			if (shooting && cnt % 15 == 0) {
				playerAttack = new PlayerAttack(playerX + 222, playerY + 25); 
				playerAttackList.add(playerAttack); 
			}

		}


		private void playerAttackProcess() {
			for (int i = 0; i < playerAttackList.size(); i++) {
				playerAttack = playerAttackList.get(i);
				playerAttack.fire();


				for (int j = 0; j < enemyList.size(); j++) {

					enemy = enemyList.get(j);
					if (playerAttack.x > enemy.x && playerAttack.x < enemy.x + enemy.width && playerAttack.y > enemy.y && playerAttack.y < enemy.y + enemy.height) {

						enemy.hp -= playerAttack.player_attack;
						playerAttackList.remove(playerAttack);
					}

					if (enemy.hp <= 0) {
						//hitSound.start(); 
						enemyList.remove(enemy);
						score += 1000;
					}

				}
			}
		}


		private void enemyAppearProcess() {
			int width;
			int height;
			if (cnt % 80 == 0) {
				if(image == null)
				{
					image = new ImageIcon("enemy_1.png").getImage();
					width = image.getWidth(null);
					height = image.getHeight(null);
					enemy = new Enemy(image, 1120,(int)(Math.random()*621),width,height); 
					enemyList.add(enemy); 
				}
				else if(image == new ImageIcon("enemy_1.png").getImage())
				{
					image = new ImageIcon("enemy_2.png").getImage();
					width = image.getWidth(null);
					height = image.getHeight(null);
					enemy = new Enemy(image, 1120,(int)(Math.random()*621),width,height); 
					enemyList.add(enemy); 
				}
				else if(image == new ImageIcon("enemy_2.png").getImage())
				{
					image = new ImageIcon("enemy_3.png").getImage();
					width = image.getWidth(null);
					height = image.getHeight(null);
					enemy = new Enemy(image, 1120,(int)(Math.random()*621),width,height); 
					enemyList.add(enemy); 
				}
				else if(image == new ImageIcon("enemy_3.png").getImage())
				{
					image = new ImageIcon("enemy.png").getImage();
					width = image.getWidth(null);
					height = image.getHeight(null);
					enemy = new Enemy(image, 1120,(int)(Math.random()*621),width,height); 
					enemyList.add(enemy); 
				}
				else if(image == new ImageIcon("enemy.png").getImage())
				{
					image = new ImageIcon("enemy_1.png").getImage();
					width = image.getWidth(null);
					height = image.getHeight(null);
					enemy = new Enemy(image, 1120,(int)(Math.random()*621),width,height); 
					enemyList.add(enemy); 
				}
			}
		}


		private void enemyMoveProcess() {
			for (int i = 0; i < enemyList.size(); i++) {

				enemy = enemyList.get(i);
				enemy.move();
			}
		}


		private void enemyAttackProcess() {

			if(level_ == 1)
			{
				enemy_attacks = 3;
			}
			else if(level_ == 2)
			{
				enemy_attacks = 5;
			}
			else if(level_ == 3)
			{
				enemy_attacks = 10;
			}	

			if (cnt % 50 == 0) {
				if(enemy_missiles == null)
				{
					enemy_missiles = new ImageIcon("enemy_attack.png").getImage();
					enemyAttack = new EnemyAttack(enemy_missiles,enemy.x - 79, enemy.y + 35);
					enemyAttackList.add(enemyAttack);
				}
				else if(enemy_missiles == new ImageIcon("enemy_attack.png").getImage())
				{
					enemy_missiles = new ImageIcon("enemy_attack_1.png").getImage();
					enemyAttack = new EnemyAttack(enemy_missiles,enemy.x - 79, enemy.y + 35);
					enemyAttackList.add(enemyAttack);
				}
				else if(enemy_missiles == new ImageIcon("enemy_attack_1.png").getImage())
				{
					enemy_missiles = new ImageIcon("enemy_attack (3).png").getImage();
					enemyAttack = new EnemyAttack(enemy_missiles,enemy.x - 79, enemy.y + 35);
					enemyAttackList.add(enemyAttack);
				}
				else if(enemy_missiles == new ImageIcon("enemy_attack (3).png").getImage())
				{
					enemy_missiles = new ImageIcon("enemy_attack.png").getImage();
					enemyAttack = new EnemyAttack(enemy_missiles,enemy.x - 79, enemy.y + 35);
					enemyAttackList.add(enemyAttack);
				}

			}

			for (int i = 0; i < enemyAttackList.size(); i++) {
				enemyAttack = enemyAttackList.get(i);
				enemyAttack.fire();
			}

			if (enemyAttack.x > playerX && enemyAttack.x < playerX + playerWidth && enemyAttack.y > playerY && enemyAttack.y < playerY + playerHeight) {
				//hitSound.start();
				playerHp -= enemy_attacks;
				enemyAttackList.remove(enemyAttack);
				if (playerHp <= 0) isOver = true; 
			}
		}


		public void gameDraw(Graphics g) {
			playerDraw(g);
			enemyDraw(g);
			infoDraw(g);
		}


		public void  infoDraw(Graphics g) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial",Font.BOLD,40));
			g.drawString(name +"'s SCORE : " + score,40,80); 


			if (isOver) {
				g.setColor(Color.BLACK);
				g.setFont(new Font("Arial",Font.CENTER_BASELINE ,50));
				g.drawString(name+"'s score: " + score,270,380);
				g.drawString("Press esc to end the game",270,460);
			}

		}


		public void playerDraw(Graphics g) {
			g.drawImage(player,playerX,playerY,null);

			g.setColor(Color.RED);
			g.fillRect(playerX - 1, playerY - 40,playerHp * 6, 20);

			for (int i = 0; i < playerAttackList.size(); i++) {
				playerAttack = playerAttackList.get(i); 
				g.drawImage(playerAttack.image,playerAttack.x,playerAttack.y,null); 
			} 
		}


		public void enemyDraw(Graphics g) {
			for (int i = 0; i < enemyList.size(); i++) {
				enemy = enemyList.get(i);
				g.drawImage(enemy.image, enemy.x, enemy.y, null ); 
				g.setColor(Color.GREEN);
				g.fillRect(enemy.x + 1, enemy.y - 40,enemy.hp * 15, 20);
			}

			for (int i = 0; i < enemyAttackList.size(); i++) {
				enemyAttack = enemyAttackList.get(i);
				g.drawImage(enemyAttack.enemy_attack_image,enemyAttack.x,enemyAttack.y,null);

			}
		}


		//public boolean isOver() {
		//	return isOver;
		//}


		public void setUp(boolean up) {
			this.up = up;
		}

		public void setDown(boolean down) {
			this.down = down;
		}

		public void setLeft(boolean left) {
			this.left = left;
		}

		public void setRight(boolean right) {
			this.right = right;
		}

		public void setShooting(boolean shooting) {
			this.shooting = shooting;
		}

	}
	class EnemyAttack {
		Image enemy_attack_image = new ImageIcon("enemy_attack (3).png").getImage();
		int x,y;


		// 난이도 올리려면 attack의 값도 올리면 된다.

		public EnemyAttack(Image image ,int x, int y) {
			this.enemy_attack_image = image;
			this.x = x;
			this.y = y;
		}


		public void fire() {
			if(level_ == 1)
			{
				this.x -= 10;
			}
			else if(level_ == 2)
			{
				this.x -= 15;
			}
			else if(level_ == 3)
			{
				this.x -= 20;
			}
		}
	}
	class Enemy {
		Image image = new ImageIcon("enemy_1.png").getImage();
		int x,y;
		int width;
		int height;
		int hp = 10;

		public Enemy(Image image, int x, int y,int width, int height) {
			this.image = image;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}


		public void move() {
			this.x -= 7;
		}

	}

}



