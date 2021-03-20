/*
	Shooting()	: JFrame, JPanel　の設定
 	proc()		: 実際の処理
 	クラスMoveObjの描画が動く流れは、
			next_x, next_yの値をsetNextMove(..)で受け取る
	->		proc()でupdate()が呼ばれ、クラスShooterのx, yが更新される
	->		update(パネルの横幅, パネルの縦幅)で次のx, yの値がパネルを超えていないかをチェックする
	-> 		proc()でrepaint()が呼ばれる
	->  	paint()でそれぞれのShootingクラスのdraw()が呼ばれる
	->		draw()で描画される
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.applet.*;
import java.util.ArrayList;

public class Shooting extends JPanel implements KeyListener{

	public final int framerate = 100;
	int width, height;
	// playerInterfaceの処理コード短縮のために
	// trueなら1 falseなら0
	public int a_pressed = 0, s_pressed = 0, d_pressed = 0, w_pressed = 0;
	public Shooter[] shooters = new Shooter[11];

	public static void main(String[] args){
		new Shooting();
	}
	public Shooting(){
		JFrame frame = new JFrame("Shooting");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500,500);
		frame.setResizable(false);
		frame.addKeyListener(this);

		this.setBackground(Color.WHITE);
		this.setSize(500,500);
		
		width  = this.getWidth();
		height = this.getHeight();

		shooters[0]  = new Shooter(250, 400, 20, 1, Color.BLUE, 0);
		shooters[1]  = new Shooter(60, 80, 20, 1, Color.LIGHT_GRAY, 1);
		shooters[2]  = new Shooter(80, 50, 20, 1, Color.BLACK, 2);
		shooters[3]  = new Shooter(90, 100, 20, 1, Color.CYAN, 3);
		shooters[4]  = new Shooter(50, 150, 20, 1, Color.GREEN, 4);
		shooters[5]  = new Shooter(300, 300, 20, 1, Color.YELLOW, 5);
		shooters[6]  = new Shooter(100, 150, 20, 1, Color.DARK_GRAY, 6);
		shooters[7]  = new Shooter(150, 120, 20, 1, Color.GRAY, 7);
		shooters[8]  = new Shooter(200, 100, 20, 1, Color.MAGENTA, 8);
		shooters[9]  = new Shooter(90, 200, 20, 1, Color.PINK, 9);
		shooters[10] = new Shooter(200, 200, 20, 1, Color.ORANGE,10);

		frame.add(this);
		frame.setVisible(true);

		proc();
	}
	private void proc(){
		while(true){
			// データの更新
			try{Thread.sleep(1000/framerate);}catch(InterruptedException e){System.out.println(e);}
			playerInterface(a_pressed, s_pressed, d_pressed, w_pressed);
			for (int i=0; i<shooters.length; i++) {

				shooters[i].update(this.width, this.height);	
			}
			//再描画
			repaint();
		}
	}
	private boolean isOverlap(MoveObj a, MoveObj b){
		/*
			aとbがactiveかつ重なっていないならfalse
			それ以外はtrue
		*/
		if(
			a.isActive() &&
			b.isActive() &&
			Math.pow((a.getR() + b.getR()), 2) < (Math.pow((b.getY() - a.getY()), 2)) + (Math.pow((b.getX() - a.getX()), 2))
			){
			return true;
		}
		return false;
	}
	private void isClisionAllShooter(){
		/*
			全てのアクティブな機体が重なっていないか調べ、重なっていた場合はその機体のdie()メソッドを呼び出す関数
		*/
		for(int i=0; i<shooters.length-1; i++){
			if(shooters[i].isActive()){
				for(int j=i+1; j<shooters.length-1; j++){
					if(shooters[j].isActive()){
						if(isOverlap(shooters[i], shooters[j])){
							shooters[i].die();
							shooters[j].die();
						}
					}
				}	
			}
		}	
	}

	private void isShotAllShooter(){
		/*
			全てのアクティブな機体に弾丸がヒットしていないか調べ、ヒットしていた場合はその機体と弾丸のdie()メソッドを呼び出す関数
			shootersのfor文(i)の中でshootersのfor文(j)を行い、shooters[j].getBullet()で弾丸を受け取り、isOberlap()する
		*/
		Bullet[] bullets;

		for(int i=0; i<shooters.length; i++){
			if(shooters[i].isActive()){
				for(int j=0; j<shooters.length; j++){
					if(i==j){break;}
					bullets = shooters[j].getBullets();
					for(int k=0; k<bullets.length; k++){
						if(isOverlap(shooters[i], bullets[k])){
							shooters[j].die();
							bullets[k].die();
						}
					}
				}
			}
		}
	}
	private void playerInterface(int a_pressed,int s_pressed,int d_pressed,int w_pressed){
		this.shooters[0].setNextMove(d_pressed - a_pressed, s_pressed - w_pressed);
	}

	@Override public void paint(Graphics g){
		// 画面をリセット
		g.clearRect(0, 0, this.width, this.height);
		// 描画
		for (int i=0; i<shooters.length; i++) {
			shooters[i].draw(g);
		}
	}
	@Override public void keyPressed(KeyEvent e){
		char key = e.getKeyChar();
		if (key == 'a'){
			a_pressed = 1;
			System.out.println("OK?");
		}
		if (key == 's'){
			s_pressed = 1;
		}
		if (key == 'd'){
			d_pressed = 1;
		}
		if (key == 'w'){
			w_pressed = 1;
		}
	}
	@Override public void keyReleased(KeyEvent e){
		char key = e.getKeyChar();
		if (key == 'a'){
			a_pressed = 0;
			System.out.println("OK?");
		}
		if (key == 's'){
			s_pressed = 0;
		}
		if (key == 'd'){
			d_pressed = 0;
		}
		if (key == 'w'){
			w_pressed = 0;
		}
	}
	@Override public void keyTyped(KeyEvent e){
		int key = e.getKeyChar();

		if (key == 'k'){
			shooters[0].shot(1,1);
		}
		else if (key == 'l'){
			shooters[0].shot(1,1);
		}
		else if (key == ';'){
			shooters[0].shot(1,1);
		}
		else if (key == 'o'){
			shooters[0].shot(1,1);
		}
		else{
			assert true;
		}
	}
}

class MoveObj{
	public final int id;
	public int x, y;
	public int speed;
	public int r;
	public Color color;
	public boolean active;	// procで判定する

	private int next_x, next_y;

	public MoveObj(int x, int y, int r, int speed, Color color, int id){
		this.x      = x;	// 機体の中心のx座標
		this.y      = y;	// 機体の中心のy座標
		this.r = r;
		this.speed  = speed;
		this.color  = color;
		this.id     = id;
		this.active = true;
		this.next_x = 0;	// 次のframeで進むxの量
		this.next_y = 0;	// 次のframeで進むyの量
	}
	public void setNextMove(int next_x, int next_y){
		this.next_x = next_x * this.speed;
		this.next_y = next_y * this.speed;
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
	public int getR(){
		return this.r;
	}
	public boolean isActive(){
		return active;
	}
	public void die(){
		this.active = false;
	}
	public void update(int width, int height){
		this.x += this.next_x;
		this.y += this.next_y;

		// 画面外に描画される場合は修正する
		for(int i=0; i<10; i++){
			if(this.x - this.r < 0){
				this.x = this.r;
			}
			if(width < this.x + this.r){
				this.x = width - this.r;
			}
			if(this.y - this.r < 0){
				this.y = this.r;
			}
			if(height < this.y + this.r){
				this.y = height - this.r;
			}
		}
	}
	public void draw(Graphics g){
		g.setColor(this.color);
		g.fillOval(this.x-this.r, this.y-this.r, this.r, this.r);
	}
}

class Bullet extends MoveObj{
	Bullet(int x, int y, int next_x, int next_y, int id){
		super(x, y, 1, 1, Color.RED, id);
	}
}

class Shooter extends MoveObj{

	ArrayList<Bullet> bullets = new ArrayList<Bullet>(0);
	public Shooter(int x, int y, int r, int speed, Color color, int id){
		super(x, y, r, speed, color, id);
	}

	public void shot(int next_x, int next_y){
		// bulletsのsizeのidを持ったBulletをbulletsに追加
		bullets.add(new Bullet(this.getX(), this.getY(), next_x, next_y, bullets.size()));
	}

	public Bullet[] getBullets(){
		return new Bullet[7];
	}
}














