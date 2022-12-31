import java.awt.*;
import java.awt.event.*;

import draw.Train;

// 列車の情報ウィンドウ
public class TrainInfoWindow {
    private Train selectedTrain;
    private Rectangle rect;
    private boolean hasDragStarted;

    public TrainInfoWindow() {
        selectedTrain = null;
        rect = new Rectangle(0, 0, 150, 50);
    }

    public void drawTrainInfo(Graphics g) {
        if (selectedTrain == null) {
            return;
        }
        if (!selectedTrain.onDuty) {
            return;
        }

        // 選択の枠
        Rectangle frameRect = selectedTrain.getRect();

        g.setColor(Color.RED);
        g.drawRect(frameRect.x, frameRect.y, frameRect.width, frameRect.height);

        // 下地
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);
        // 枠
        g.setColor(selectedTrain.getTypeColor());
        g.drawRect(rect.x, rect.y, rect.width, rect.height);

        // 列車番号 種別
        String trainID = selectedTrain.trainData.getTimeTable().trainID;
        String trainType = selectedTrain.trainData.getTimeTable().trainType;

        g.drawString(trainID + " " + trainType, rect.x + 10, rect.y + 20);
        g.drawString(generateTrainNameStr(selectedTrain), rect.x + 10, rect.y + 30);
        g.drawString(selectedTrain.getTerminalName() + "行", rect.x + 10, rect.y + 40);
    }

    private String generateTrainNameStr(Train train) {
        String str = "";
        String trainName = train.trainData.getTimeTable().trainName;
        if (trainName.isEmpty()) {
            return str;
        }
        str += "\n" + trainName;

        // 号
        String trainNo = train.trainData.getTimeTable().trainNo;
        if (trainNo.isEmpty()) {
            return str;
        }
        str += " " + trainNo + "号";

        return str;
    }

    public void setTrain(Train train) {
        this.selectedTrain = train;
        if (train == null) {
            this.rect.x = 0;
            this.rect.y = 0;
        } else {
            // ウィンドウを移動できるようにするため、値渡しする
            this.rect.x = train.getRect().x;
            this.rect.y = train.getRect().y;
        }
    }

    // --------------------------------------------------------------------------------
    // マウスイベント
    // --------------------------------------------------------------------------------
    public boolean getOnMouse(MouseEvent e) {
        if (selectedTrain == null) {
            return false;
        } else {
            return this.rect.contains(new Point(e.getX(), e.getY()));
        }
    }

    public boolean dragWindow(MouseEvent e) {
        if (!hasDragStarted) {
            return false;
        } else {
            this.rect.x = e.getX();
            this.rect.y = e.getY();
            return true;
        }
    }

    public void dragStarted(MouseEvent e) {
        hasDragStarted = getOnMouse(e);
    }

    public void dragFinished(MouseEvent e) {
        hasDragStarted = false;
    }

}