/** Created by Jack Chen at 12/16/2014 */
package org.apache.poi.xssf.usermodel;


import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xslf.usermodel.XSLFRelation;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTTwoCellAnchor;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.STEditAs;

import java.lang.reflect.Method;

/**
 * 这个类最好是放在所有bean初始化完成后使用
 *
 * @author Jack Chen
 */
public class ExcelUtils {
    private static final Method newShapeIdMethod;
    private static final Method createTwoCellAnchorMethod;

    static {
        try {
            newShapeIdMethod = XSSFDrawing.class.getDeclaredMethod("newShapeId");
            newShapeIdMethod.setAccessible(true);

            createTwoCellAnchorMethod = XSSFDrawing.class.getDeclaredMethod("createTwoCellAnchor", XSSFClientAnchor.class);
            createTwoCellAnchorMethod.setAccessible(true);
        } catch(Exception e) {
            throw new ReportExportException(e.getMessage(), e);
        }
    }

    public static void addPicture(XSSFDrawing patriarch, Cell cell, String imageUrl) throws Exception {
        //图片显示45个像素,两边居中
        XSSFClientAnchor anchor2 = new XSSFClientAnchor((int) (7.5 * XSSFShape.EMU_PER_PIXEL), (int) (7.5 * XSSFShape.EMU_PER_PIXEL), (int) (52.5 * XSSFShape.EMU_PER_PIXEL), (int) (52.5 * XSSFShape.EMU_PER_PIXEL), cell.getColumnIndex(), cell.getRowIndex(), cell.getColumnIndex(), cell.getRowIndex());
        anchor2.setAnchorType(XSSFClientAnchor.MOVE_AND_RESIZE);
        PackageRelationship x2 = patriarch.getPackagePart().addExternalRelationship(imageUrl, XSLFRelation.IMAGES.getRelation());
        createPicture(patriarch, anchor2, x2);
    }

    public static XSSFPicture createPicture(XSSFDrawing drawing, XSSFClientAnchor anchor, PackageRelationship rel) throws Exception {
        long shapeId = newShapeId(drawing);
        CTTwoCellAnchor ctAnchor = createTwoCellAnchor(drawing, anchor);
        ctAnchor.setEditAs(STEditAs.TWO_CELL);
        CTPicture ctShape = ctAnchor.addNewPic();
        ctShape.set(XSSFPicture.prototype());
        ctShape.getNvPicPr().getCNvPr().setId(shapeId);
        XSSFPicture shape = new XSSFPicture(drawing, ctShape);
        shape.anchor = anchor;
        shape.getCTPicture().getBlipFill().getBlip().setLink(rel.getId());
        return shape;
    }

    private static long newShapeId(XSSFDrawing drawing) throws Exception {
        return (Long) newShapeIdMethod.invoke(drawing);
    }

    private static CTTwoCellAnchor createTwoCellAnchor(XSSFDrawing drawing, XSSFClientAnchor anchor) throws Exception {
        return (CTTwoCellAnchor) createTwoCellAnchorMethod.invoke(drawing, anchor);
    }
}
