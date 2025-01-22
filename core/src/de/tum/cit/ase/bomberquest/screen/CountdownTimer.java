package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.utils.Timer;
import de.tum.cit.ase.bomberquest.BomberQuestGame;

public class CountdownTimer {
    private float timeLeft; //剩余时间
    private Timer.Task timerTask;
    BomberQuestGame game;
    private boolean isGameOver = false;
    private boolean pause;

    public CountdownTimer(float initialTime) {
        this.timeLeft = initialTime;
    }

    public CountdownTimer(float timeLeft, Timer.Task timerTask, boolean isGameOver) {
        this.timeLeft = timeLeft;
        this.timerTask = timerTask;
        this.isGameOver = isGameOver;
    }

    public CountdownTimer() {
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    //启用计时器
    public void start(){
       timerTask = new Timer.Task() {
           public void run() {
               if(pause
               ){
                   return;
               }

               if(timeLeft > 0) {
                   timeLeft -= 1;
               }else{
                   endGame();
               }
           }
       } ;
        Timer.schedule(timerTask,1,1);
    }

    //增加时间
    public void addtime(float extraTime){
        if(!isGameOver){
            timeLeft += extraTime;
            System.out.println("Add " + extraTime + " second(s), timeleft now: " + timeLeft + " second(s)" );
        }
    }

    //停止计时
    public void stop(){
        if(timerTask != null){
          timerTask.cancel();
        }
    }

    //倒计时结束触发事件
    private void endGame(){
        isGameOver = true;
        stop();
        game.goToVictoryAndGameOver(false);
        // System.out.println("Game failed! Time out!");//可写其他游戏失败界面
        //showGameOverScreen();
    }

    //获取剩余时间

    public float getTimeLeft() {
        return timeLeft;
    }

    public Timer.Task getTimerTask() {
        return timerTask;
    }

   public boolean isGameOver() {
        return isGameOver;
    }
}
