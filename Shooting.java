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
	// coonfig
	final boolean CONFIC_USR_PLAY = true;
	//final boolean CONFIG_ZOMBI_ENEMY = false;

	final int framerate = 100;
	int width, height;
	int timeCnt = 0;
	// playerInterfaceの処理コード短縮のために
	// trueなら1 falseなら0
	int a_pressed = 0, s_pressed = 0, d_pressed = 0, w_pressed = 0, j_pressed = 0, k_pressed = 0, l_pressed = 0, i_pressed = 0;
	Shooter[] shooters = new Shooter[11];

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

		shooters[0]  = new Shooter(250, 400, 10, Color.BLUE);
		shooters[1]  = new Shooter(60, 80, 10, Color.LIGHT_GRAY);
		shooters[2]  = new Shooter(80, 50, 10, Color.BLACK);
		shooters[3]  = new Shooter(90, 100, 10, Color.CYAN);
		shooters[4]  = new Shooter(50, 150, 10, Color.GREEN);
		shooters[5]  = new Shooter(400, 300, 10, Color.YELLOW);
		shooters[6]  = new Shooter(100, 300, 10, Color.DARK_GRAY);
		shooters[7]  = new Shooter(150, 110, 10, Color.GRAY);
		shooters[8]  = new Shooter(100, 100, 10, Color.MAGENTA);
		shooters[9]  = new Shooter(90, 100, 10, Color.PINK);
		shooters[10] = new Shooter(100, 100, 10, Color.ORANGE);

		frame.add(this);
		frame.setVisible(true);

		proc();
	}
	private void proc(){
		boolean canShot = true;
		while(true){
			timeCnt += 1000/framerate;
			if (timeCnt > 100){
				canShot = true;
				timeCnt = 0;
			}
			// データの更新
			try{Thread.sleep(1000/framerate);}catch(InterruptedException e){System.out.println(e);}
			for (int i=0; i<shooters.length; i++) {
				if (CONFIC_USR_PLAY){if (i == 0){playerInterface(canShot);}}
				else{shooters[i].ai_interface(canShot);}
			}
			//再描画
			repaint();
			// 機体の当たり判定を行う
			isClisionAllShooter();
			// 弾丸の当たり判定を行う
			isShotAllShooter();
			canShot = false;
		}
	}
	private void playerInterface(boolean canShot){
		this.shooters[0].update(d_pressed - a_pressed, s_pressed - w_pressed, this.width, this.height);
		if (canShot){	// 大体0.1秒ごと
			if (l_pressed - j_pressed != 0 || k_pressed - i_pressed != 0){
				this.shooters[0].shot(l_pressed - j_pressed, k_pressed - i_pressed);
			}
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
			// (a.r = b.r)^2 > (a.y-b.x)^2 + (a.x - b.x)^2
			Math.pow(a.getR() + b.getR(), 2) > ((Math.pow(b.getY() - a.getY(), 2)) + (Math.pow(b.getX() - a.getX(), 2)))
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
							shooters[i].die();
							bullets[k].die();
						}
					}
				}
			}
		}
	}
	@Override public void paint(Graphics g){
		// 画面をリセット
		g.clearRect(0, 0, this.width, this.height);
		// 描画
		for (int i=0; i<shooters.length; i++){
			shooters[i].draw(g);
		}
	}
	@Override public void keyPressed(KeyEvent e){
		char key = e.getKeyChar();
		if (key == 'a'){a_pressed = 1;}
		if (key == 's'){s_pressed = 1;}
		if (key == 'd'){d_pressed = 1;}
		if (key == 'w'){w_pressed = 1;}
		if (key == 'j'){j_pressed = 1;}
		if (key == 'k'){k_pressed = 1;}
		if (key == 'l'){l_pressed = 1;}
		if (key == 'i'){i_pressed = 1;}
	}
	@Override public void keyReleased(KeyEvent e){
		char key = e.getKeyChar();
		if (key == 'a'){a_pressed = 0;}
		if (key == 's'){s_pressed = 0;}
		if (key == 'd'){d_pressed = 0;}
		if (key == 'w'){w_pressed = 0;}
		if (key == 'j'){j_pressed = 0;}
		if (key == 'k'){k_pressed = 0;}
		if (key == 'l'){l_pressed = 0;}
		if (key == 'i'){i_pressed = 0;}
	}
	@Override public void keyTyped(KeyEvent e){}
}
class MoveObj{
	public int x, y, speed, r;
	public Color color;
	public boolean active;
	public int next_x, next_y;

	public MoveObj(int x, int y, int r, Color color){
		this.x      = x;	// 機体の中心のx座標
		this.y      = y;	// 機体の中心のy座標
		this.r = r;
		this.color  = color;
		this.active = true;
		this.next_x = 0;	// 次のframeで進むxの量
		this.next_y = 0;	// 次のframeで進むyの量
	}
	public void setNextMove(int next_x, int next_y){
		this.next_x = next_x;
		this.next_y = next_y;
	}
	public int getX(){return this.x;}
	public void setX(int x){this.x = x;}
	public int getY(){return this.y;}
	public void setY(int y){this.y = y;}
	public int getR(){return this.r;}
	public boolean isActive(){return active;}
	public void die(){this.active = false;}
	public void update(int next_x, int next_y, int width, int height){
		this.x += next_x;
		this.y += next_y;
		// 画面外に描画される場合は修正する
		if(this.x - this.r < 0){this.x = this.r;}
		if(width < this.x + this.r){this.x = width - this.r;}
		if(this.y - this.r < 0){this.y = this.r;}
		if(height < this.y + this.r){this.y = height - this.r;}
		_update();
	}
	// 継承先でupdateに加える処理
	public void _update(){}
	public void draw(Graphics g){
		if (active){
			g.setColor(this.color);
			g.fillOval(this.x-this.r, this.y-this.r, 2*this.r, 2*this.r);
		}
	}
}
class Bullet extends MoveObj{
	Bullet(int x, int y, int next_x, int next_y){
		super(x, y, 3, Color.RED);
		setNextMove(next_x, next_y);
	}
	public void update(){
		this.x += next_x;
		this.y += next_y;
	}
}
class Shooter extends MoveObj{
	ArrayList<Bullet> bullets = new ArrayList<Bullet>(0);
	public Shooter(int x, int y, int r, Color color){super(x, y, r, color);}

	public void shot(int next_x, int next_y){
		// もしnext_x, next_yが両方とも0なら発射しない
		if (!active){return;}
		bullets.add(new Bullet(this.getX(), this.getY(), next_x, next_y));
	}
	public Bullet[] getBullets(){return bullets.toArray(new Bullet[bullets.size()]);}
	public void _update(){
		//自分の発射した弾丸のupdate()をする
		for (int i=0; i<bullets.size(); i++) {bullets.get(i).update();}
	}
	public void draw(Graphics g){
		// MoveObj同様に自分を描画する
		if (active){
			g.setColor(this.color);
			g.fillOval(this.x-this.r, this.y-this.r, 2*this.r, 2*this.r);
			// インスタンス化した弾丸を描画する
			// 最終的には、画面外へでたものは配列から排除する仕様にすること
		}
		for (int i=0; i<bullets.size(); i++) {bullets.get(i).draw(g);}
	}
	public void ai_interface(boolean canShot){
		/*
		this.shooters[0].update(next_x, next_y, this.width, this.height);
		if (canShot){	// 大体0.1秒ごと
			if (l_pressed - j_pressed != 0 || k_pressed - i_pressed != 0){
				this.shooters[0].shot(l_pressed - j_pressed, k_pressed - i_pressed);
			}
		}
		*/
	}
}
