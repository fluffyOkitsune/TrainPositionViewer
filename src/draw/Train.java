package draw;

import java.awt.*;
import java.awt.event.*;

import data.Time;
import data.line_data.LineData;
import data.train_data.TrainData;

public class Train {
    TrainData trainData;

    private Rectangle rect;
    private Image image;

    public Train(TrainData trainData, Image image, LineData lineData, Time time) {
        this.trainData = trainData;
        this.image = image;

        rect = calcTrainSizeRect(calcTrainPos(trainData, lineData, time));
    }

    // 描画する列車の位置を計算する
    private Point calcTrainPos(TrainData trainData, LineData lineData, Time currentTime) {
        float pos = trainData.calcPos(lineData, currentTime);
        return lineData.calcPositionOnLinePath(pos);
    }

    // 描画する列車の領域を計算する
    private Rectangle calcTrainSizeRect(Point pos) {
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        return new Rectangle(pos.x - width / 2, pos.y - height / 2, width, height);
    }

    public void draw(Graphics g) {
        g.drawImage(image, rect.getLocation().x, rect.getLocation().y, null);
    }

    public boolean getOnMouse(MouseEvent e) {
        return rect.contains(new Point(e.getX(), e.getY()));
    }

    @Override
    public String toString(){
        return "列車番号: " + trainData.trainID;
    }
}
