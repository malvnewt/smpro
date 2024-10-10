package smpro.app.utils;

import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.colorspace.PdfShading;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.DoubleBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.*;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import javafx.application.Application;
import javafx.stage.Stage;
import smpro.app.ResourceUtil;

import java.io.*;
import java.security.spec.RSAOtherPrimeInfo;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

import static com.itextpdf.kernel.events.PdfDocumentEvent.END_PAGE;
import static com.itextpdf.kernel.events.PdfDocumentEvent.START_PAGE;

public class DocumentBase extends Application {
    private final String docPath;
    public DocumentBase(String filePath) {
        this.docPath= filePath;



    }

    ///////////////////////////////////////////////
    ///////////////////////////////////////////////

    public void buildMarkSheet(List<HashMap<String, Object>> classes) {
        try(FileOutputStream os = new FileOutputStream(docPath)) {
            PdfHelper helper = new PdfHelper(os, PageSize.A4);

            helper.save();
            openDoc();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void buildClassList(List<String> classids,List<String> tablefields) {
        System.out.println("::buildClassList()");
        try(FileOutputStream os = new FileOutputStream(docPath)) {
            PdfHelper helper = new PdfHelper(os, PageSize.A4);
            helper.buildClassList( classids, tablefields);
            helper.save();
            openDoc();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public void buildEmployeeList(List<HashMap<String,Object>> empdata,List<String> tablefields) {
        System.out.println("::buildemployeeList()");
        try(FileOutputStream os = new FileOutputStream(docPath)) {
            PdfHelper helper = new PdfHelper(os, PageSize.A4);
            helper.buildEmployeeList( empdata, tablefields);
            helper.save();
            openDoc();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    ///////////////////////////////////////////////
    ///////////////////////////////////////////////



    public void openDoc() { getHostServices().showDocument(this.docPath); }
    @Override
    public void start(Stage stage) throws Exception {
    }
}








class PdfHelper{
    private final PdfWriter writer;
    private final Document doc;
    private final PdfDocument pdfdoc;

    private final Image logoImage;

    private final HashMap<String,Object> baseInfo;

    private double docWidth;

    private final float hpadding = 20;
    private final float toppadding = 10;
    private final float bottomPadding = 20;


    //borders
    private final SolidBorder solidGrayBorder = new SolidBorder(ColorConstants.GRAY, 1, 0.8f);
    private final SolidBorder solidBorder = new SolidBorder(Color.convertRgbToCmyk(new DeviceRgb(28,28,28)), 1, 0.8f);
    private final SolidBorder darkBorder = new SolidBorder(ColorConstants.BLACK, 1, 0.8f);
    private final DoubleBorder doubleBorder = new DoubleBorder(ColorConstants.BLACK, 1, 0.8f);
    private final DashedBorder dashedBorder = new DashedBorder(ColorConstants.BLACK, 1, 0.8f);

    //fonts
//    PdfFont nimbusFont = PdfFontFactory.createFont(ResourceUtil.getResourceAsStream("css/fonts/Nimbus Roman.ttf").readAllBytes(), PdfEncodings.WINANSI,true);
    PdfFont nimbusFont = PdfFontFactory.createFont(FontProgramFactory.createFont("src/main/resources/smpro/app/css/fonts/Nimbus Roman.ttf"), PdfEncodings.WINANSI, true);


    PdfFont courierBold = PdfFontFactory.createFont("Courier-Bold", PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
    PdfFont courier = PdfFontFactory.createFont("Courier", PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
    PdfFont arialUni = PdfFontFactory.createFont(FontProgramFactory.createFont("C:/Windows/Fonts/arialuni.ttf"), PdfEncodings.IDENTITY_H);
    PdfFont helvetica = PdfFontFactory.createFont("Helvetica", PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);


    Color docGray = Color.convertRgbToCmyk(new DeviceRgb(220, 220, 220));
    Color reportHeaderBg = Color.convertRgbToCmyk(new DeviceRgb(143, 227, 247));


    float mainfontSize = 10f;

    private PageSize pgsize;


    public PdfHelper(OutputStream os, PageSize... pageSizes) throws IOException {
        this.writer = new PdfWriter(os);
         this.pdfdoc=new PdfDocument(writer);
        this.doc = new Document(pdfdoc);

        doc.setMargins(toppadding,hpadding,bottomPadding,hpadding);

        if (pageSizes.length == 0) {
            this.pdfdoc.setDefaultPageSize(PageSize.A4);
            this.docWidth = PageSize.A4.getWidth();
            this.pgsize = PageSize.A4;
        } else {
            this.pdfdoc.setDefaultPageSize(pageSizes[0]);
            this.docWidth = pageSizes[0].getWidth();
            this.pgsize = pageSizes[0];
        }

        try {
            PreparedStatement ps = PgConnector.getConnection().prepareStatement("select logo_bytes from base where id=1");
            InputStream logoIs =   PgConnector.readBinarydata(ps);
            this.logoImage =new Image( ImageDataFactory.create(logoIs.readAllBytes()));
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        this.baseInfo = PgConnector.fetch("select * from base", PgConnector.getConnection()).get(0);

    }

    //// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    public void buildMarksheet() {
        //build header
        String title = String.format("%s %s", Translator.getIntl("annual_marksheet").toUpperCase(), PgConnector.getFielorBlank(baseInfo, "academic_year"));
//        buildGenericHeader(title);
    }

    public void buildClassList(List<String> classids,List<String> tablefields) throws IOException {

        int lanscapeThreshold = 6;
        if (tablefields.size() > lanscapeThreshold) {
            this.pdfdoc.setDefaultPageSize(PageSize.A4.rotate());
            this.docWidth = PageSize.A4.rotate().getWidth();
            this.pgsize = PageSize.A4.rotate();

        }


        //set page event handler
        this.pdfdoc.addEventHandler(START_PAGE, new PageFooterEventHandler(this.doc, "page",
               Translator.getIntl("printed_on")+ " "+ ProjectUtils.getFormatedDateTime(new Date().getTime(),DateFormat.getDateTimeInstance(0,2,
                       Translator.getLocale()))));


        List<HashMap<String, Object>> dbTrades = PgConnector.fetch("select * from trades", PgConnector.getConnection());
        for (String cid : classids) {
            HashMap<String,Object> cobj =    ProjectUtils.getObject(cid, "classes");
            List<HashMap<String, Object>> students = PgConnector.fetch(String.format("select * from students where classid=%d order by firstname,lastname",
                    Integer.parseInt(cid)), PgConnector.getConnection());
            //build header


            Paragraph pageTitle = new Paragraph().setFontSize(mainfontSize + 2);
            pageTitle.add(new Text(Translator.getIntl("sorted_classlists").toUpperCase()+" : ").setFont(courier));
            pageTitle.add(new Text(Store.UnicodeSumnbol.blank).setFont(helvetica));
            pageTitle.add(new Text(PgConnector.getFielorBlank(cobj, "classname").toUpperCase() ).setFont(courierBold));
            pageTitle.add(new Text(Store.UnicodeSumnbol.blank).setFont(helvetica));
            pageTitle.add(new Text(String.format("%s(%s%s)",Store.UnicodeSumnbol.blank,PgConnector.getFielorBlank(cobj, "class_abbreviation"),
                    Store.UnicodeSumnbol.bullet)).setFont(courierBold));
            pageTitle.add(new Text(String.format("\t\t\t\t%s : ",Translator.getIntl("enrolled").toUpperCase())).setFont(courier));
            pageTitle.add(new Text(Store.UnicodeSumnbol.blank).setFont(helvetica));
            pageTitle.add(new Text(String.valueOf(students.size()) ).setFont(courierBold));


            buildGenericHeader(pageTitle);


            //table data

     students.forEach(s->{
                long dob = PgConnector.getNumberOrNull(s, "date_of_birth").longValue();
                long adb = PgConnector.getNumberOrNull(s, "admission_date").longValue();
                String trade = PgConnector.getFielorBlank(s, "trade");

                s.replace("date_of_birth", ProjectUtils.getFormatedDate(dob, DateFormat.getDateInstance(DateFormat.MEDIUM, Translator.getLocale())));
                s.replace("admission_date", ProjectUtils.getFormatedDate(adb, DateFormat.getDateInstance(DateFormat.MEDIUM, Translator.getLocale())));

         HashMap<String, Object> foundTrade = dbTrades.stream().filter(t ->
                 Objects.equals(PgConnector.getFielorBlank(t, "trade_name"), trade)).findFirst().orElse(null);
         assert foundTrade != null;
         s.replace("trade", PgConnector.getFielorBlank(foundTrade, "trade_abbreviation"));

         s.replace("firstname", PgConnector.getFielorBlank(s, "firstname").toUpperCase());
         s.replace("lastname", PgConnector.getFielorBlank(s, "lastname").toUpperCase());
         s.replace("parent_one", ProjectUtils.capitalize(PgConnector.getFielorBlank(s, "parent_one")));

         boolean repeater = Boolean.parseBoolean(String.valueOf(s.get("repeater")));
         String repString = repeater ? Translator.getIntl("yes").toUpperCase() : Translator.getIntl("no").toUpperCase();
         s.replace("repeater", repString);




            });

     // data keys = tablefields



            //table header
            List<HashMap<String, Object>> headerdata = new ArrayList<>();
            for (String field : tablefields) {
                String name = switch (field) {
                    case "admission_date" -> Translator.getIntl("admission");
                    case "date_of_birth" -> Translator.getIntl("birth");
                    default -> Translator.getIntl(field);
                };

                String icon = switch (field) {
                    case "firstname" -> Store.UnicodeSumnbol.person;
//                    case "date_of_birth" -> Translator.getIntl("birth");
                    default -> "";
                };

                headerdata.add(new HashMap<>(Map.of(
                        "hname", name,
                        "rspan", 1,
                        "cspan", 1,
                        "hicon",icon
                )));
            }

            //table colwidths
            float[] colwidths = new float[tablefields.size()];
            colwidths[0]=2.5f;
            colwidths[1]=2.5f;
            colwidths[2]=0.8f;
            for (int i = 3; i <=5; i++) colwidths[i]=1.5f;

            for (int i = 6; i < tablefields.size(); i++) colwidths[i]=1;


            //build table
            HashMap<Integer, Float> fontSizes = new HashMap<>();
            for (int i = 0; i < headerdata.size(); i++) fontSizes.put(i, mainfontSize - 1.5f);

            Table t = buildGenericTable(headerdata, colwidths, students,tablefields,List.of(2,3),fontSizes);
            this.doc.add(t);

            this.doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));


        }

    }
    public void buildEmployeeList(List<HashMap<String,Object>> employeeData,List<String> tablefields) throws IOException {

        int lanscapeThreshold = 5;
        if (tablefields.size() > lanscapeThreshold) {
            this.pdfdoc.setDefaultPageSize(PageSize.A4.rotate());
            this.docWidth = PageSize.A4.rotate().getWidth();
            this.pgsize = PageSize.A4.rotate();

        }

        //set page event handler
        this.pdfdoc.addEventHandler(START_PAGE, new PageFooterEventHandler(this.doc, "page",
               Translator.getIntl("printed_on")+ " "+ ProjectUtils.getFormatedDateTime(new Date().getTime(),DateFormat.getDateTimeInstance(0,2,
                       Translator.getLocale()))));

        HashMap<String,Object> baseObj =    ProjectUtils.getObject("1", "base");
        //build header
        Paragraph pageTitle = new Paragraph().setFontSize(mainfontSize + 2);
        pageTitle.add(new Text(Translator.getIntl("list_of_emp").toUpperCase()+" : ").setFont(courier));
        pageTitle.add(new Text(Store.UnicodeSumnbol.blank).setFont(helvetica));
        pageTitle.add(new Text(PgConnector.getFielorBlank(baseObj, "academic_year").toUpperCase() ).setFont(courierBold));
        pageTitle.add(new Text(Store.UnicodeSumnbol.blank).setFont(helvetica));
        pageTitle.add(new Text(String.format("\t\t\t\t%s : ",Translator.getIntl("employee_count").toUpperCase())).setFont(courier));
        pageTitle.add(new Text(Store.UnicodeSumnbol.blank).setFont(helvetica));
        pageTitle.add(new Text(String.valueOf(employeeData.size()) ).setFont(courierBold));

        buildGenericHeader(pageTitle);

        //table data
        employeeData.forEach(s->{
            long addedOn = PgConnector.getNumberOrNull(s, "date_added").longValue();

            s.replace("date_added", ProjectUtils.getFormatedDate(addedOn, DateFormat.getDateInstance(DateFormat.MEDIUM, Translator.getLocale())));

            s.replace("first_lastname", PgConnector.getFielorBlank(s, "first_lastname").toUpperCase());
            s.replace("employee_category", ProjectUtils.capitalize(Translator.getIntl(PgConnector.getFielorBlank(s, "employee_category"))));
            s.replace("time_factor", ProjectUtils.capitalize(Translator.getIntl(PgConnector.getFielorBlank(s, "time_factor"))));
            s.replace("department", ProjectUtils.capitalize(PgConnector.getFielorBlank(s, "department")));

        });

        //table header
        List<HashMap<String, Object>> headerdata = new ArrayList<>();
        for (String field : tablefields) {
            String name = switch (field) {
                case "first_lastname" -> Translator.getIntl("employee_name");
                case "employee_category" -> Translator.getIntl("category");
                case "date_added" -> Translator.getIntl("added_on");
                case "time_factor" -> Translator.getIntl("time_scope");
                default -> Translator.getIntl(field);
            };

            String icon = switch (field) {
                case "firstname" -> Store.UnicodeSumnbol.person;
//                    case "date_of_birth" -> Translator.getIntl("birth");
                default -> "";
            };

            headerdata.add(new HashMap<>(Map.of(
                    "hname", name,
                    "rspan", 1,
                    "cspan", 1,
                    "hicon",icon
            )));
        }

        //table colwidths
        float[] colwidths = new float[tablefields.size()];
        colwidths[0]=2f;
        colwidths[1]=1.5f;
        colwidths[2]=1.8f;
        colwidths[3]=0.8f;
        colwidths[4]=1.3f;

        for (int i = 5; i < tablefields.size(); i++) colwidths[i]=1;


        //build table
        HashMap<Integer, Float> fontSizes = new HashMap<>();
        for (int i = 0; i < headerdata.size(); i++) fontSizes.put(i, mainfontSize - 1.5f);

        Table t = buildGenericTable(headerdata, colwidths, employeeData,tablefields,List.of(3),fontSizes);
        this.doc.add(t);

        this.doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));




    }

    //// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    //////////////////////////////////////////////////////  PDF SECTIONS
    //////////////////////////////////////////////////////

    public void buildGenericHeader(Paragraph titlePara,boolean... centerTitle) {
        //reset font
        nimbusFont =courierBold;
        float[] colwidthsPortrait = new float[]{1.5f, 2, 1, 1,1,1,2,1};
        float[] colwidthsLandscape = new float[]{1.5f,2,2,2,2,2,2,1};

        float portraitTotal=10.5f;
        float landscapetotal=14.5f;

        double imagecellwidth;

        double qrwidth = this.docWidth == PageSize.A4.getWidth()? ((double) 1 /portraitTotal) * docWidth-2*hpadding : ((double) 1 /landscapetotal) * docWidth-2*hpadding;


        Table table;
        if (this.docWidth == PageSize.A4.rotate().getWidth()) {
            table = new Table(UnitValue.createPercentArray(colwidthsLandscape));
            imagecellwidth = ((double) 1 / landscapetotal) * (docWidth - hpadding * 2);
        } else {
            table = new Table(UnitValue.createPercentArray(colwidthsPortrait));
            imagecellwidth = ((double) 1.5 / portraitTotal) * (docWidth - hpadding * 2);

        }
        table.setWidth((float) (docWidth - (hpadding * 2)));
        // insert logo
        Cell imageCell = new Cell(3,1).add(logoImage.setWidth((float) imagecellwidth).setAutoScale(true));
        table.addCell(imageCell.setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE).setBorder(solidBorder));

        //school name
        String schoolname = PgConnector.getFielorBlank(baseInfo, "school_name").toUpperCase();
        Cell namecell = new Cell(1, 7).add(new Paragraph(schoolname).setPadding(5f).setFont(nimbusFont).setFontSize(mainfontSize + 8f).
                        setHorizontalAlignment(HorizontalAlignment.CENTER).setTextAlignment(TextAlignment.CENTER))
                .setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE);
        table.addCell(namecell.setPadding(5f).setBorder(solidBorder));


        Cell telcell = new Cell(1,3);
        Paragraph telpara = new Paragraph().setFont(nimbusFont).setFontSize(mainfontSize).setPadding(5f);
        telpara.add(new Text("TEL ").setFont(courier));
        telpara.add(new Text(PgConnector.getFielorBlank(baseInfo,"lineone")+" / "+PgConnector.getFielorBlank(baseInfo,"linetwo")).setFont(courierBold));
        telpara.add(new Text(" "+Store.UnicodeSumnbol.phone).setFontSize(mainfontSize+4).setFont(arialUni));

        table.addCell(telcell.setBorder(solidBorder).add(telpara.setFixedLeading(11f)));



        String year = PgConnector.getFielorBlank(baseInfo, "academic_year");
        Paragraph yearpara = new Paragraph().setFontSize(mainfontSize).setFont(nimbusFont).setPadding(5f).setFixedLeading(11f);
        yearpara.add(new Text(Translator.getIntl("academic_year")+" " ).setFont(courier)).add(new Text(year).setFontSize(mainfontSize).setFont(courierBold));

        Cell yearcell = new Cell(1,3).add(yearpara.setVerticalAlignment(VerticalAlignment.MIDDLE)).setVerticalAlignment(VerticalAlignment.MIDDLE);
        table.addCell(yearcell.setBorder(solidBorder));


        Cell qrcell = new Cell(2, 1).setBorder(solidBorder);

        String data = String.format("%s \n%s\nNgwa marvin newton\nSMPRO-2022\n\n" +
                " Robust and flexible Relational Database management system adapting to the needs of any institution" +
                "Thanks for purchasing", schoolname, year);

//        qrcell.add(img).setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE);
        qrcell.setNextRenderer(new QrCellRender(qrcell,data));
        table.addCell(qrcell);


        String address = PgConnector.getFielorBlank(baseInfo, "address");
        Paragraph addresspara = new Paragraph().setFontSize(mainfontSize).setFont(nimbusFont).setFixedLeading(11f);
        addresspara.add(new Text(Translator.getIntl("address")+ " ").
                setFont(courier)).add(new Text(ProjectUtils.capitalize(address)).setFontSize(mainfontSize).setFont(courierBold));

        addresspara.add(new Text( "\t" + Translator.getIntl("pobox")+" ").setFont(courier));
        addresspara.add(new Text(PgConnector.getFielorBlank(baseInfo,"pobox")+" "+ProjectUtils.capitalize(PgConnector.getFielorBlank(baseInfo,"town_city"))).setFont(courierBold));

        Cell addresscell = new Cell(1,6).add(addresspara.setPadding(5f).setTextAlignment(TextAlignment.CENTER)).setBorder(solidBorder);
        addresspara.add(new Text(" "+Store.UnicodeSumnbol.location).setFontSize(mainfontSize+5).setFont(arialUni));

        table.addCell(addresscell.setVerticalAlignment(VerticalAlignment.MIDDLE));


        this.doc.add(table.setMarginBottom(10));

//        Paragraph titlePara = new Paragraph(bottomTitle).setFont(courierBold).
//                setFontSize(mainfontSize + 3).setTextAlignment(centerTitle.length>0 ?TextAlignment.CENTER :TextAlignment.LEFT);
        titlePara.setFontSize(mainfontSize + 3).setTextAlignment(centerTitle.length>0 ?TextAlignment.CENTER :TextAlignment.LEFT);
        doc.add(titlePara.setMarginBottom(8));




    }

    public Table buildGenericTable(
            List<HashMap<String, Object>> headerData,float[] colwidths,
            List<HashMap<String, Object>> tableData,List<String> keys,
            List<Integer> centerCols,HashMap<Integer,Float> colfontSizes ) {

        Table table = new Table(UnitValue.createPercentArray(colwidths));
        table.setWidth((float) (this.docWidth - 2 * hpadding));

        // teble header
        for (HashMap<String, Object> headerMap : headerData) {
            String headername = PgConnector.getFielorBlank(headerMap, "hname");
            String headericon = PgConnector.getFielorBlank(headerMap, "hicon");
            int rspan = PgConnector.getNumberOrNull(headerMap,"rspan").intValue();
            int cspan = PgConnector.getNumberOrNull(headerMap,"cspan").intValue();

            Paragraph headerpara = new Paragraph().setFontSize(mainfontSize + 1).setFont(courierBold).setPadding(5f);
//            headerpara.add(new Text(headericon).setFont(arialUni).setFontSize(mainfontSize+2));
            headerpara.add(new Text(" "+headername).setFont(courierBold).setFontSize(mainfontSize+1));
            Cell headerCell = new Cell(rspan,cspan).add(headerpara.setVerticalAlignment(VerticalAlignment.MIDDLE)).setVerticalAlignment(VerticalAlignment.MIDDLE);
            //set endereer
            TableHeaderCellRenderer roundCornersRenderer = new TableHeaderCellRenderer(headerCell,
                    new Color[]{
                            docGray

            } );
            headerCell.setNextRenderer(roundCornersRenderer);

            table.addHeaderCell(headerCell);
        }

        //table body
        for (HashMap<String, Object> dataitem : tableData) {
            for (String k : keys) {
                int index =  keys.indexOf(k);
                int rspan = PgConnector.getNumberOrNull(headerData.get(index), "rspan").intValue();
                int cspan = PgConnector.getNumberOrNull(headerData.get(index), "cspan").intValue();
                String dataval = PgConnector.getFielorBlank(dataitem, k);

                float fontSize = colfontSizes.containsKey(index) ? colfontSizes.get(index) : mainfontSize;

                Paragraph dataPara = new Paragraph(dataval).setFontSize(fontSize).setPadding(3f).setFixedLeading(8f);
                if (centerCols.contains(index)) dataPara.setTextAlignment(TextAlignment.CENTER);
                Cell dataCell = new Cell(rspan, cspan).add(dataPara);

                table.addCell(dataCell);

            }


        }




        return table;
    }


    //////////////////////////////////////////////////////
    public String padd(String content, int count) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i <count; i++) s.append(content);

        return s.toString();
    }

    //////////////////////////////////////////////////////


    public void save() {
        this.doc.flush();
        this.doc.close();
    }






}



//////////////////////////////////////////   UTILITY CLASSES ////////////////////////////////////////
//
///**
// * Cell event class for applying rounded corners and gradient backgrounds to
// * table cells.
// */



class TableHeaderCellRenderer extends CellRenderer {
    private final Color[] colorFill;
    private final float radius;

    private Cell cell;




    public TableHeaderCellRenderer(
            Cell modelelement,
            Color[] colorFill,
            float... radius
    ) {

        super(modelelement);
        modelelement.setBorder(Border.NO_BORDER);

        this.cell=modelelement;
        this.colorFill = colorFill;

        this.radius = radius.length == 0 ? 5f : radius[0];


    }


    @Override
    public void drawBackground(DrawContext drawContext) {

       PdfCanvas cb =  drawContext.getCanvas();
        PdfDocument doc =  drawContext.getDocument();

        cb.saveState();
        float radOffset = 0f;
        float radVOffset = 1f;

        Rectangle rect = getOccupiedAreaBBox();

        // Adjust left and right positions to fix visible gaps
        float left = rect.getLeft() + radOffset;
        float bottom = rect.getBottom() + radOffset;

        //draw border
        cb.setStrokeColor(Color.convertRgbToCmyk(new DeviceRgb(28, 28, 28))).setLineWidth(1);
//        cb.roundRectangle(left, bottom, rect.getWidth() - radOffset, rect.getHeight() - radOffset, this.radius);

        //manually curve top ends
        double curv = 0.44769999384880066;
        float width = rect.getWidth();
        float height = rect.getHeight();

        cb.moveTo(left, bottom+radVOffset);
        cb.lineTo(left + rect.getWidth(), bottom+radVOffset);
//        cb.curveTo(x + width - radius * 0.44769999384880066, y, x + width, y + radius * 0.44769999384880066, x + width, y + radius);
        cb.lineTo(left + rect.getWidth(), bottom + rect.getHeight() - radius);
        cb.curveTo(left + width, bottom + height - radius * curv, left + width - radius * curv, bottom + height, left + width - radius, bottom + height);
        cb.lineTo(left + radius, bottom + height);
        cb.curveTo(left + radius * curv, bottom + height, left, bottom + height - radius * curv, left, bottom + height - radius);
        cb.lineTo(left, bottom+radVOffset);
//        cb.curveTo(left, bottom + radius * curv, left + radius * curv, bottom, left + radius, bottom);

        if(colorFill.length < 2) {
            cb.setFillColor(colorFill[0]);
        } else {
            PdfShading.Axial sh = new PdfShading.Axial(new PdfDeviceCs.Rgb(), 0, rect.getHeight(), colorFill[0].getColorValue(), 0, 0, colorFill[1].getColorValue());
            PdfPattern.Shading pattern = new PdfPattern.Shading(sh);

            cb.setFillColorShading(pattern);
        }
        // Fill it up!
        cb.fillStroke();

//        super.draw(drawContext);
        cb.restoreState();


    }



}

class QrCellRender extends CellRenderer {
    private final String data;


    public QrCellRender(Cell modelElement, String data) {
        super(modelElement);
        this.data = data;
//        modelElement.setBorderBottom(new DashedBorder(ColorConstants.BLACK, 0.5f, 0.5f));
    }

    @Override
    public void draw(DrawContext drawContext) {
//        super.draw(drawContext);
        PdfCanvas cv = drawContext.getCanvas();
        Rectangle rect = getOccupiedAreaBBox();
        PdfDocument pdfdoc = drawContext.getDocument();

        Canvas canvas = new Canvas(cv, rect);


        BarcodeQRCode code = new BarcodeQRCode(data);
        PdfFormXObject pdfFormXObject = code.createFormXObject(ColorConstants.BLACK,pdfdoc );
        Image img = new Image(pdfFormXObject).setWidth(rect.getWidth()).setHeight(rect.getHeight()).setAutoScale(true);


        canvas.add(img.setHorizontalAlignment(HorizontalAlignment.CENTER));


    }
}



 class PageFooterEventHandler implements IEventHandler {
    protected Document doc;
    private final String middlecontent;
    private final String endcontent;
    PdfFont gunt = PdfFontFactory.createFont(FontProgramFactory.createFont("src/main/resources/smpro/app/css/fonts/guntz-bold.otf"), PdfEncodings.WINANSI, true);
     PdfFont arialUni = PdfFontFactory.createFont(FontProgramFactory.createFont("C:/Windows/Fonts/arialuni.ttf"), PdfEncodings.IDENTITY_H);

    PdfFont footerFont = PdfFontFactory.createFont("Helvetica-Oblique", PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
    PdfFont helveticaBold = PdfFontFactory.createFont("Helvetica-Bold", PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);

    public PageFooterEventHandler(Document doc,String middleContent,String endContent) throws IOException {
        this.doc = doc;
        this.middlecontent = middleContent;
        this.endcontent = endContent;
    }
    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfCanvas canvas = new PdfCanvas(docEvent.getPage());
        Rectangle pageSize = docEvent.getPage().getPageSize();

        int pagecount = docEvent.getDocument().getNumberOfPages();
        int curPageIndex = docEvent.getDocument().getPageNumber(docEvent.getPage());


        canvas.beginText();

        float leftStart = 10;
        float bottom = 5;
        float midstart=pageSize.getWidth()/2 - 85;
        float endStart =  pageSize.getWidth() / 4 -45;

        String middleString = middlecontent;
        if (middlecontent.equalsIgnoreCase("page")) {
//            middleString = String.format("%s %d/%d", Translator.getIntl("page"), curPageIndex, pagecount);
            middleString = String.format("%s %d", Translator.getIntl("page"), curPageIndex);
        }

        canvas.moveText(leftStart, bottom).setFontAndSize(footerFont, 7).showText("Powered by ")
                .setFontAndSize(gunt, 7).showText("SMPRO ").setFontAndSize(arialUni,7).showText(Store.UnicodeSumnbol.andCopy).setFontAndSize(gunt,7).showText("2021")
                .moveText(105,0).setFontAndSize(arialUni,12).showText(Store.UnicodeSumnbol.phone).setFontAndSize(helveticaBold,8).showText(" 671686616 ")
                .moveText(midstart-40, 0).setFontAndSize(footerFont, 7).showText(middleString).moveText(endStart, 0).showText(endcontent).endText().release();

    }
}