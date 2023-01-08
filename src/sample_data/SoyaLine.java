package sample_data;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.line_data.EasyPathPoint;
import data.line_data.LineData;
import data.line_data.LineSegmentPath;
import data.line_data.SingleTrackLinePath;
import data.train_data.TrainData;

public class SoyaLine extends LineData {
        private Image imageIconLocal;
        private Image imageIconRapid;
        private Image imageIconLtd;

        public SoyaLine() {
                super();
                try {
                        imageIconLocal = ImageIO.read(new File("icon/h54n.png"));
                        Image img = ImageIO.read(new File("icon/h100.png"));
                        imageIconRapid = LineData.createEdgedImage(img, Color.ORANGE, 2);
                        imageIconLtd = ImageIO.read(new File("icon/h261w.png"));
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        private static final Color LINE_COLOR = new Color(140, 64, 42);

        @Override
        public Color getLineColor() {
                return LINE_COLOR;
        }

        @Override
        public String getLineName() {
                return "宗谷本線";
        }

        @Override
        protected String getStationDataCsvPath() {
                return "time_table/souya_line_station.csv";
        }

        @Override
        protected String getTimeTableOutCsvPath() {
                return "time_table/souya_line_weekdays_out.csv";
        }

        @Override
        protected String getTimeTableInCsvPath() {
                return "time_table/souya_line_weekdays_in.csv";
        }

        private Point origin = new Point(200, 200);

        @Override
        public Point calcPosOnLinePath(float dist, Direction direction) {
                Point offset;
                if (direction == Direction.OUTBOUND) {
                        offset = new Point(+0, -20);
                } else {
                        offset = new Point(-0, +20);
                }

                // 駅の長さ割合
                float staLen = 0.05f;

                EasyPathPoint[] epp = {
                                // 旭川 - 永山
                                LineSegmentPath.getInstance(getDistProportion(3),
                                                new Point(origin.x + 0, origin.y + offset.y),
                                                new Point(origin.x + 93, origin.y + offset.y)),
                                // 永山 - 比布
                                SingleTrackLinePath.getInstance(getDistProportion(6),
                                                offset, staLen,
                                                new Point(origin.x + 93, origin.y + 0),
                                                new Point(origin.x + 171, origin.y + 0)),
                                // 比布 - 蘭留
                                SingleTrackLinePath.getInstance(getDistProportion(8),
                                                offset, staLen,
                                                new Point(origin.x + 171, origin.y + 0),
                                                new Point(origin.x + 228, origin.y + 0)),
                                // 蘭留 - 塩狩
                                SingleTrackLinePath.getInstance(getDistProportion(9),
                                                offset, staLen,
                                                new Point(origin.x + 228, origin.y + 0),
                                                new Point(origin.x + 284, origin.y + 0)),
                                // 塩狩 - 和寒
                                SingleTrackLinePath.getInstance(getDistProportion(10),
                                                offset, staLen,
                                                new Point(origin.x + 284, origin.y + 0),
                                                new Point(origin.x + 363, origin.y + 0)),
                                // 和寒 - 剣淵
                                SingleTrackLinePath.getInstance(getDistProportion(12),
                                                offset, staLen,
                                                new Point(origin.x + 363, origin.y + 0),
                                                new Point(origin.x + 452, origin.y + 0)),
                                // 剣淵 - 士別
                                SingleTrackLinePath.getInstance(getDistProportion(14),
                                                offset, staLen,
                                                new Point(origin.x + 452, origin.y + 0),
                                                new Point(origin.x + 539, origin.y + 0)),
                                // 士別 - 風連
                                SingleTrackLinePath.getInstance(getDistProportion(18),
                                                offset, staLen,
                                                new Point(origin.x + 539, origin.y + 0),
                                                new Point(origin.x + 681, origin.y + 0)),
                                // 風連 - 名寄
                                SingleTrackLinePath.getInstance(getDistProportion(20),
                                                offset, staLen,
                                                new Point(origin.x + 681, origin.y + 0),
                                                new Point(origin.x + 762, origin.y + 0)),
                                // 名寄 - 美深
                                SingleTrackLinePath.getInstance(getDistProportion(26),
                                                offset, staLen,
                                                new Point(origin.x + 762, origin.y + 0),
                                                new Point(origin.x + 1052, origin.y + 0)),
                                // 美深 - 豊清水
                                SingleTrackLinePath.getInstance(getDistProportion(30),
                                                offset, staLen,
                                                new Point(origin.x + 1052, origin.y + 0),
                                                new Point(origin.x + 1179, origin.y + 0)),
                                // 豊清水 - 音威子府
                                SingleTrackLinePath.getInstance(getDistProportion(33),
                                                offset, staLen,
                                                new Point(origin.x + 1179, origin.y + 0),
                                                new Point(origin.x + 1293, origin.y + 0)),
                                // 音威子府 - 佐久
                                SingleTrackLinePath.getInstance(getDistProportion(35),
                                                offset, staLen,
                                                new Point(origin.x + 1293, origin.y + 0),
                                                new Point(origin.x + 1536, origin.y + 0)),
                                // 佐久 - 天塩中川
                                SingleTrackLinePath.getInstance(getDistProportion(36),
                                                offset, staLen,
                                                new Point(origin.x + 1536, origin.y + 0),
                                                new Point(origin.x + 1619, origin.y + 0)),
                                // 天塩中川 - 雄信内
                                SingleTrackLinePath.getInstance(getDistProportion(40),
                                                offset, staLen,
                                                new Point(origin.x + 1619, origin.y + 0),
                                                new Point(origin.x + 1835, origin.y + 0)),
                                // 雄信内 - 幌延
                                SingleTrackLinePath.getInstance(getDistProportion(44),
                                                offset, staLen,
                                                new Point(origin.x + 1835, origin.y + 0),
                                                new Point(origin.x + 1994, origin.y + 0)),
                                // 幌延 - 豊富
                                SingleTrackLinePath.getInstance(getDistProportion(46),
                                                offset, staLen,
                                                new Point(origin.x + 1994, origin.y + 0),
                                                new Point(origin.x + 2159, origin.y + 0)),
                                // 豊富 - 兜沼
                                SingleTrackLinePath.getInstance(getDistProportion(48),
                                                offset, staLen,
                                                new Point(origin.x + 2159, origin.y + 0),
                                                new Point(origin.x + 2309, origin.y + 0)),
                                // 兜沼 - 抜海
                                SingleTrackLinePath.getInstance(getDistProportion(50),
                                                offset, staLen,
                                                new Point(origin.x + 2309, origin.y + 0),
                                                new Point(origin.x + 2450, origin.y + 0)),
                                // 抜海 - 南稚内
                                SingleTrackLinePath.getInstance(getDistProportion(51),
                                                offset, staLen,
                                                new Point(origin.x + 2450, origin.y + 0),
                                                new Point(origin.x + 2567, origin.y + 0)),
                                // 南稚内 - 稚内
                                SingleTrackLinePath.getInstance(getDistProportion(52),
                                                offset, staLen,
                                                new Point(origin.x + 2567, origin.y + 0),
                                                new Point(origin.x + 2594, origin.y + 0)),
                                // 終わり
                                LineSegmentPath.getInstance(Float.MAX_VALUE,
                                                new Point(origin.x + 2594, origin.y + 0),
                                                new Point(origin.x + 2594, origin.y + 0))
                };
                return generateEasyPathPoint(epp, dist);
        }

        @Override
        public Image getIconImg(TrainData trainData) {
                switch (trainData.getTimeTable().trainType) {
                        case "特急":
                                return imageIconLtd;
                        case "快速":
                                return imageIconRapid;
                        default:
                                return imageIconLocal;
                }
        }

        @Override
        public Color getTypeColor(TrainData trainData) {
                switch (trainData.getTimeTable().trainType) {
                        case "特急":
                                return Color.RED;
                        case "快速":
                                return Color.ORANGE;
                        default:
                                return LINE_COLOR;
                }
        }
}