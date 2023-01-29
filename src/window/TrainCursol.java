package window;

import java.awt.*;
import java.awt.image.*;

import draw.Train;

// 選択した列車を指し示す枠
public class TrainCursol {
    private Image cursolImg;
    private Train selectedTrain;

    private final static int ANIM_MOVING_TH = 20;
    private boolean playMoveAnim;
    private int animMoving;
    private float posX, posY;
    private float velX, velY;

    public TrainCursol() {
        playMoveAnim = false;
        posX = 0.0f;
        posY = 0.0f;
        velX = 0.0f;
        velY = 0.0f;
    }

    // --------------------------------------------------------------------------------
    // 描画処理
    // --------------------------------------------------------------------------------
    private static final Color COLOR_TRAINSPARENT = new Color(0, 0, 0, 0);
    private static final int EDGE_SIZE = 4;

    public void drawTrainCursol(Graphics g) {
        if (selectedTrain == null) {
            return;
        }
        if (!selectedTrain.onDuty) {
            return;
        }

        assert (cursolImg != null);

        if (playMoveAnim && animMoving < ANIM_MOVING_TH) {
            // 前に選択した列車から移動するアニメーション
            // 速度を加算する方法にすることでカーソル移動中に別の列車を選択しても自然に移動する
            posX += velX;
            posY += velY;
            animMoving++;
        } else {
            posX = selectedTrain.getRect().getLocation().x;
            posY = selectedTrain.getRect().getLocation().y;
        }

        g.setColor(Color.RED);
        g.drawImage(cursolImg, (int) posX - EDGE_SIZE, (int) posY - EDGE_SIZE, null);
    }

    // カーソル画像を作成する
    private static Image createCursolImg(Image image, Color edgeColor) {
        // 外側部分を取得
        BufferedImage imgOutside = new BufferedImage(
                image.getWidth(null) + 2 * EDGE_SIZE,
                image.getHeight(null) + 2 * EDGE_SIZE,
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics imgOutsideG = imgOutside.getGraphics();
        imgOutsideG.drawImage(image, 0, 0,
                image.getWidth(null) + 2 * EDGE_SIZE,
                image.getHeight(null) + 2 * EDGE_SIZE, null);
        imgOutsideG.dispose();

        // 内側部分を取得
        BufferedImage imgInside = new BufferedImage(
                image.getWidth(null) + 2 * EDGE_SIZE,
                image.getHeight(null) + 2 * EDGE_SIZE,
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics imgInsideG = imgInside.getGraphics();
        imgInsideG.drawImage(image, EDGE_SIZE, EDGE_SIZE, null);
        imgInsideG.dispose();

        // 描画
        for (int x = 0; x < imgOutside.getTileWidth(); x++) {
            for (int y = 0; y < imgOutside.getTileHeight(); y++) {
                if (imgOutside.getRGB(x, y) != 0) {
                    if (imgInside.getRGB(x, y) != 0) {
                        imgOutside.setRGB(x, y, COLOR_TRAINSPARENT.getRGB());
                    } else {
                        imgOutside.setRGB(x, y, edgeColor.getRGB());
                    }
                }
            }
        }
        imgInside.flush();
        return imgOutside;
    }

    // --------------------------------------------------------------------------------
    // マウスイベント
    // --------------------------------------------------------------------------------
    public void selectTrain(Train train) {
        // 列車が選択されている状態で他の列車を選択された場合はカーソル移動アニメーションが処理される
        if (selectedTrain == null) {
            playMoveAnim = false;
        } else {
            playMoveAnim = true;
        }

        selectedTrain = train;

        if (train == null) {
            return;
        } else {
            // カーソル画像を作成
            cursolImg = createCursolImg(train.getImage(), Color.RED);

            // カーソル移動アニメーション処理
            animMoving = 0;

            Point posTarget = train.getRect().getLocation();

            velX = (posTarget.x - posX) / ANIM_MOVING_TH;
            velY = (posTarget.y - posY) / ANIM_MOVING_TH;
        }
    }
}
