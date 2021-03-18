// enemy_id is based by variable name

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
//import javafx.scene.media.AudioClip;
import java.applet.*;

public class Shooting extends JFrame implements KeyListener{

	//config
	// player ve enemy?
	public final boolean one_for_all = false;
	// No Clear
	public final boolean mozaiku = false;
	// display paramater window?
	public final boolean pram_window = true;
	



	private static final long serialVersionUID = 1L;
	private static final int flesh_rate = 10;
	int width, height;

	public Player player;
	public Enemy enemy1;
	public Enemy enemy2;
	public Enemy enemy3;
	public Enemy enemy4;
	public Enemy enemy5;
	public Enemy enemy6;
	public Enemy enemy7;
	public Enemy enemy8;
	public Enemy enemy9;
	public Enemy enemy10;

	public Enemy[] enemies = new Enemy[10];
	public Bullet[] bullets = new Bullet[10];
	
	public boolean a_pressed = false;
	public boolean s_pressed = false;
	public boolean d_pressed = false;
	public boolean w_pressed = false;

	public static ParmWindow paramater_window;

	public static void main(String[] args){
		new Shooting();
	}

	public Shooting(){
		this.setTitle("Shooting");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500,500);
		this.setBackground(Color.WHITE);
		this.setResizable(false);
		this.addKeyListener(this);

		this.setVisible(true);

		if(pram_window){
			paramater_window = new ParmWindow();
		}

		this.requestFocus();

		width = getWidth();
		height = getHeight();

		player = new Player(250,400,20,20,1,Color.BLUE,0);
		
		enemy1 = new Enemy(60,80,20,20,1,Color.LIGHT_GRAY,1);
		enemy2 = new Enemy(80,50,20,20,1,Color.BLACK,2);
		enemy3 = new Enemy(90,100,20,20,1,Color.CYAN,3);
		enemy4 = new Enemy(50,150,20,20,1,Color.GREEN,4);
		enemy5 = new Enemy(300,300,20,20,1,Color.YELLOW,5);
		enemy6 = new Enemy(100,150,20,20,1,Color.DARK_GRAY,6);
		enemy7 = new Enemy(150,120,20,20,1,Color.GRAY,7);
		enemy8 = new Enemy(200,100,20,20,1,Color.MAGENTA,8);
		enemy9 = new Enemy(90,200,20,20,1,Color.PINK,9);
		enemy10 = new Enemy(200,200,20,20,1,Color.ORANGE,10);

		enemies[0] = enemy1;
		enemies[1] = enemy2;
		enemies[2] = enemy3;
		enemies[3] = enemy4;
		enemies[4] = enemy5;
		enemies[5] = enemy6;
		enemies[6] = enemy7;
		enemies[7] = enemy8;
		enemies[8] = enemy9;
		enemies[9] = enemy10;

		mainProcess();
	}

	private void mainProcess(){
		int timeCnt = 0;
		try{Thread.sleep(3000);}catch(InterruptedException e){System.out.println(e);}
		this.requestFocus();
		while(true){
			
			try{Thread.sleep(flesh_rate);}catch(InterruptedException e){System.out.println(e);}

			// refresh screen
			if(!mozaiku){
				clear();	
			}
			repaint();

			// check whether player and enemy collide
			isCollideAllShooter();

			// check whether shooter and bullet collide
			isShotAllShooter();

			// update data
			timeCnt += flesh_rate;
			if(timeCnt > 2000){	// 20sec
				timeCnt = 0;
				player.addMagazine();
			}

			player.update(a_pressed, s_pressed, d_pressed, w_pressed);
			for(int i=0; i<enemies.length; i++){
				enemies[i].update();
			}
		}
	}

	private boolean isOverlap(MoveObj a, MoveObj b){

		if(!a.getActive() || !b.getActive()){
			return false;
		}
		if(a.getX() < b.getX()){
			if(a.getXX() < b.getX()){
				return false;
			}
		}
		else{
			if(b.getXX() < a.getX()){
				return false;
			}	
		}
		if(a.getY() < b.getY()){
			if(a.getYY() < b.getY()){
				return false;
			}
		}
		else{
			if(b.getYY() < a.getY()){
				return false;
			}	
		}
		return true;
	}

	private void isCollideAllShooter(){
		// player
		if (player.getActive()){
			for(int i = 0; i < enemies.length; i++){
				if(isOverlap(player, enemies[i])){
					player.dead();
					enemies[i].dead();
				}
			}	
		}

		// enemies
		if(one_for_all){
			for(int i = 0; i < enemies.length -1; i++){
				if(enemies[i].getActive()){
					for(int j = i + 1; j < enemies.length - 1; j++){
						if(enemies[j].getActive()){
							if(isOverlap(enemies[i], enemies[j])){
								enemies[i].dead();
								enemies[j].dead();
							}
						}
					}	
				}
			}	
		}
	}

	private void isShotAllShooter(){

		// player
		if (player.getActive()){
			for(int i=0; i<enemies.length; i++){
				bullets = enemies[i].getBullets();
				for(int j=0; j<bullets.length; j++){
					if(isOverlap(player, bullets[j])){
						player.dead();
						bullets[j].dead();
					}
				}
			}
		}

		// enemies
		for(int i=0; i<enemies.length; i++){
			if(enemies[i].getActive()){
				// player's bullet
				bullets = player.getBullets();
				for(int j=0; j<bullets.length; j++){
					if(isOverlap(enemies[i], bullets[j])){
						enemies[i].dead();
						bullets[j].dead();
						player.kill(i+1);
					}
				}
				// enemy's bullet
				if(one_for_all){
					for(int j=0; j<enemies.length; j++){
						if (i==j){
							break;
						}
						bullets=enemies[i].getBullets();
						for(int k=0; k<bullets.length; k++){
							if(isOverlap(enemies[i], bullets[k])){
								enemies[i].dead();
								bullets[k].dead();
							}
						}
					}
				}
			}
		}
	}

	private void clear(){
		Graphics g = getGraphics();
		g.setColor(getBackground());
		g.fillRect(0,0,this.width,this.height);
		g.dispose();
	}

	public void paint(Graphics g){
		if(player.getX() < 0){
			player.setX(0);
		}
		if(this.width < player.getXX()){
			player.setX(this.width - player.getWidth());
		}
		if(player.getY() < 22){
			player.setY(22);
		}
		if(this.height < player.getYY()){
			player.setY(this.height - player.getHeight());
		}
		player.draw(g);	
	
		
		for(int i=0; i<10; i++){
			
			if(enemies[i].getX() < 0){
				enemies[i].setX(0);
			}
			if(this.width < enemies[i].getXX()){
				enemies[i].setX(this.width - enemies[i].getWidth());
			}
			if(enemies[i].getY() < 22){
				enemies[i].setY(22);
			}
			if(this.height < enemies[i].getYY()){
				enemies[i].setY(this.height - enemies[i].getHeight());
			}
			enemies[i].draw(g);
		}
	}

	

	@Override public void keyPressed(KeyEvent e){
		char key = e.getKeyChar();
		if (key == 'a'){
			a_pressed = true;
		}
		if (key == 's'){
			s_pressed = true;
		}
		if (key == 'd'){
			d_pressed = true;
		}
		if (key == 'w'){
			w_pressed = true;
		}
	}
	@Override public void keyReleased(KeyEvent e){
		char key = e.getKeyChar();
		if (key == 'a'){
			a_pressed = false;
		}
		if (key == 's'){
			s_pressed = false;
		}
		if (key == 'd'){
			d_pressed = false;
		}
		if (key == 'w'){
			w_pressed = false;
		}
	}
	@Override public void keyTyped(KeyEvent e){
		int key = e.getKeyChar();

		if (key == 'k'){
			player.shot(Const.LEFT);

		}
		else if (key == 'l'){
			player.shot(Const.DOWN);
		}
		else if (key == ';'){
			player.shot(Const.RIGHT);
		}
		else if (key == 'o'){
			player.shot(Const.UP);
		}
		else{
			assert true;
		}
	}
}

class ParmWindow extends JFrame{

	JLabel num_of_bullet_label;
	JLabel player_active;
	JLabel enemy1_active;
	JLabel enemy2_active;
	JLabel enemy3_active;
	JLabel enemy4_active;
	JLabel enemy5_active;
	JLabel enemy6_active;
	JLabel enemy7_active;
	JLabel enemy8_active;
	JLabel enemy9_active;
	JLabel enemy10_active;

	JLabel[] enemies_active = new JLabel[10];

	JLabel kill_score_label;

	ParmWindow(){
		this.setTitle("Paramater");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(200,500);
		this.setLocation(550, 0);
		this.setResizable(false);
		this.setFocusable(false);

		JPanel inner = new JPanel();

		inner.setLayout(new GridLayout(13,1));
		inner.setBackground(Color.WHITE);
		

		player_active = new JLabel("PLAYER: Active");
		enemy1_active = new JLabel("ENEMY1: Active");
		enemy2_active = new JLabel("ENEMY2: Active");
		enemy3_active = new JLabel("ENEMY3: Active");
		enemy4_active = new JLabel("ENEMY4: Active");
		enemy5_active = new JLabel("ENEMY5: Active");
		enemy6_active = new JLabel("ENEMY6: Active");
		enemy7_active = new JLabel("ENEMY7: Active");
		enemy8_active = new JLabel("ENEMY8: Active");
		enemy9_active = new JLabel("ENEMY9: Active");
		enemy10_active = new JLabel("ENEMY10: Active");
		num_of_bullet_label = new JLabel("MAGAZINE: 5");
		kill_score_label = new JLabel("SCORE: 0");

		enemies_active[0] = enemy1_active;
		enemies_active[1] = enemy2_active;
		enemies_active[2] = enemy3_active;
		enemies_active[3] = enemy4_active;
		enemies_active[4] = enemy5_active;
		enemies_active[5] = enemy6_active;
		enemies_active[6] = enemy7_active;
		enemies_active[7] = enemy8_active;
		enemies_active[8] = enemy9_active;
		enemies_active[9] = enemy10_active;

		inner.add(player_active);
		inner.add(enemy1_active);
		inner.add(enemy2_active);
		inner.add(enemy3_active);
		inner.add(enemy4_active);
		inner.add(enemy5_active);
		inner.add(enemy6_active);
		inner.add(enemy7_active);
		inner.add(enemy8_active);
		inner.add(enemy9_active);
		inner.add(enemy10_active);
		inner.add(num_of_bullet_label);
		inner.add(kill_score_label);

		this.add(inner);
		this.setVisible(true);
	}

	public void dead(int id){
		if(id==0){
			player_active.setText("PLAYER: DEAD");
		}
		else{
			enemies_active[id-1].setText("ENEMY" + String.valueOf(id) + ": DEAD");
		}
	}

	public void shot(int id, int num_of_bullet){
		if(id==0){
			num_of_bullet_label.setText("MAGAZINE: " + String.valueOf(num_of_bullet));
		}
	}

	public void addMagazine(int id, int num_of_bullet){
		if(id==0){
			num_of_bullet_label.setText("MAGAZINE: " + String.valueOf(num_of_bullet));
		}
	}

	public void kill(int kill_score){
		kill_score_label.setText("SCORE: " + String.valueOf(kill_score));
	}
}

class Const{
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int UP = 2;
	public static final int DOWN = 3;	
}

class MoveObj extends Const{
	public final int id;
	public int x = 0;
	public int y = 0;
	public int s = 3;
	public int width = 50;
	public int height = 50;
	public Color color = Color.BLACK;
	public boolean active = true;

	public MoveObj(int ini_x, int ini_y, int ini_height, int ini_width, int s, Color ini_color, int id){
		this.x = ini_x;
		this.y = ini_y;
		this.height = ini_height;
		this.width = ini_width;
		this.s = s;
		this.color = ini_color;
		this.id = id;
	}

	public void move(int direction){
		if(direction == LEFT){
			this.x -= s;
		}
		else if(direction == RIGHT){
			this.x += s;
		}
		else if(direction == UP){
			this.y -= s;
		}
		else if(direction == DOWN){
			this.y += s;
		}
		else{
			while(true){
				System.out.println("Bug in class: MoceObj method: move");
			}
		}
	}
	public int getX(){
		return this.x;
	}
	public void setX(int x){
		this.x = x;
	}
	public int getY(){
		return this.y;
	}
	public void setY(int y){
		this.y = y;
	}
	public int getWidth(){
		return this.width;
	}
	public void setWidth(int width){
		this.width = width;
	}
	public int getHeight(){
		return this.height;
	}
	public void setHeight(int height){
		this.height = height;
	}
	public Color getColor(){
		return this.color;
	}
	public void setColor(Color color){
		this.color = color;
	}
	public boolean getActive(){
		return this.active;
	}
	public void setActive(boolean active){
		this.active = active;
	}
	public int getXX(){
		return this.x + this.width;
	}
	public int getYY(){
		return this.y + this.height;
	}
	public int getCenterX(){
		return this.x + (this.width / 2);
	}
	public int getCenterY(){
		return this.y + (this.height / 2);
	}

	public void draw(Graphics g){
		g.setColor(color);
		g.fillRect(x ,y, height, width);
	}
	public void dead(){
		this.setActive(false);
		Shooting.paramater_window.dead(this.id);
	}
}

class Bullet extends MoveObj{
	private int direction;

	// bullet speed is 20
	Bullet(int id){
		super(999,999,333,333,5,Color.RED, id);
		this.setActive(false);
	}

	public void init(int x, int y, int direction){
		this.setActive(true);
		this.direction = direction;
		if (direction == LEFT || direction == RIGHT){
			this.setWidth(5);
			this.setHeight(10);
		}
		else{
			this.setWidth(10);
			this.setHeight(5);	
		}
		this.setX(x);
		this.setY(y);
	}

	public void update(){
		move(this.direction);
	}

}

class ShotObj extends MoveObj{

	public Bullet bullet1;
	public Bullet bullet2;
	public Bullet bullet3;
	public Bullet bullet4;
	public Bullet bullet5;
	public Bullet bullet6;
	public Bullet bullet7;
	public Bullet bullet8;
	public Bullet bullet9;
	public Bullet bullet10;
	public Bullet[] bullets = new Bullet[10];

	public static final int max_magazine = 5;
	public int magazine = max_magazine;
	public static final int reload_time = 30000;

	public int kill_score = 0;

	public ShotObj(int ini_x, int ini_y, int ini_height, int ini_width, int ini_s, Color ini_color, int id){
		super(ini_x, ini_y, ini_height, ini_width, ini_s, ini_color, id);

		bullet1 = new Bullet(1);
		bullet2 = new Bullet(2);
		bullet3 = new Bullet(3);
		bullet4 = new Bullet(4);
		bullet5 = new Bullet(5);
		bullet6 = new Bullet(6);
		bullet7 = new Bullet(7);
		bullet8 = new Bullet(8);
		bullet9 = new Bullet(9);
		bullet10 = new Bullet(10);
		bullets[0] = bullet1;
		bullets[1] = bullet2;
		bullets[2] = bullet3;
		bullets[3] = bullet4;
		bullets[4] = bullet5;
		bullets[5] = bullet6;
		bullets[6] = bullet7;
		bullets[7] = bullet8;
		bullets[8] = bullet9;
		bullets[9] = bullet10;
	}

	public void draw(Graphics g){
		// this
		if(active){
			g.setColor(color);
			g.fillRect(x ,y, height, width);
		}

		// bullet
		for(int i = 0; i < max_magazine - magazine; i++){
			if(bullets[i].getActive()){
				bullets[i].draw(g);	
			}
		}
	}

	public void shot(int direction){
		if(!active){
			return;
		}
		if (magazine == 0){
			System.out.println("No bullet");
		}
		else{
			bullets[max_magazine - magazine].init(this.getCenterX(), this.getCenterY(), direction);
			magazine--;
			Shooting.paramater_window.shot(id, magazine);
		}
	}

	public Bullet[] getBullets(){
		int tmp = max_magazine - magazine;
		Bullet[] ret = new Bullet[tmp];
		for (int i = 0; i < tmp; i++){
			ret[i] = bullets[i];
		}
		return ret;
	}

	public void addMagazine(){
		if(magazine < max_magazine){
			magazine++;
			Shooting.paramater_window.addMagazine(id, magazine);
		}
	}
}

class Player extends ShotObj{
	public boolean a_pressed = false;
	public boolean s_pressed = false;
	public boolean d_pressed = false;
	public boolean w_pressed = false;

	public Player(int ini_x, int ini_y, int ini_height, int ini_width, int ini_s, Color ini_color, int id){
		super(ini_x, ini_y, ini_height, ini_width, ini_s, ini_color, id);
	}
	public void update(boolean a_pressed, boolean s_pressed, boolean d_pressed, boolean w_pressed){
		if(a_pressed){
			move(LEFT);
		}
		if(s_pressed){
			move(DOWN);
		}
		if(d_pressed){
			move(RIGHT);
		}
		if(w_pressed){
			move(UP);
		}

		for(int i=0; i < max_magazine - magazine; i++){
			bullets[i].update();
		}
	}

	public void kill(int id){
		kill_score++;
		Shooting.paramater_window.kill(kill_score);
	}
}

class Enemy extends ShotObj{

	public int cnt=0;

	public Enemy(int ini_x, int ini_y, int ini_height, int ini_width, int ini_s, Color ini_color, int id){
		super(ini_x, ini_y, ini_height, ini_width, ini_s, ini_color, id);
	}
	public void update(){
		Random rnd = new Random();
		if(rnd.nextBoolean()){
			move(UP);
			if(rnd.nextInt(5000) < 1){
				this.shot(UP);
			}
		}
		if(rnd.nextBoolean()){
			move(LEFT);
			if(rnd.nextInt(5000) < 1){
				this.shot(LEFT);
			}
		}
		if(rnd.nextBoolean()){
			move(RIGHT);
			if(rnd.nextInt(5000) < 1){
				this.shot(RIGHT);
			}
		}
		if(rnd.nextBoolean()){
			move(DOWN);
			if(rnd.nextInt(5000) < 1){
				this.shot(DOWN);
			}
		}
		for(int i=0; i < max_magazine - magazine; i++){
			bullets[i].update();
		}
	} 
}