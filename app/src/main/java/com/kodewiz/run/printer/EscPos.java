/*
package com.kodewiz.run;


*/
/**
 * ESC/POS library for communicating with thermal printers
 *
 * ##copyright##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 *
 * @author		##author##
 * @modified	##date##
 * @version		##version##
 *//*



import java.io.IOException;
import java.io.OutputStream;


*/
/**
 * Copyright (c) 2011 Joseph Bergen metalab(at)harvard
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * ESCPos is designed to facilitate serial communication between Processing and
 * ESC/POS thermal printers. This library is designed to make it easier to send
 * simple (and not so simple) commands to the mOutputStream more efficiently and cleanly
 * than would be otherwise possible
 *
 * @example Hello
 *
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 *//*



public class ESCPos {

    OutputStream mOutputStream;  // Create object from Serial class


*/
/**
     * a Constructor, usually called in the setup() method in your sketch to
     * initialize and start the library.
     *
     * @example Hello
     *//*


    public ESCPos(OutputStream thisPort) {
        this.mOutputStream = thisPort;
    }

    public void sayHello() throws IOException{

        mOutputStream.write(0x1B);
        mOutputStream.write("@".getBytes());
        mOutputStream.write("Hello World".getBytes());
        mOutputStream.write(0x1B);
        mOutputStream.write("d".getBytes());
        mOutputStream.write(6);

        System.out.println("Hello World");
    }


*/
/**
     * return the version of the library.
     *
     * @return String
     *//*


    public static String version() {
    }


*/
/**
     *
     * reusable init esc code
     *
     *//*


    public void escInit(){
        mOutputStream.write(0x1B);
        mOutputStream.write("@");
    }


*/
/**
     * resets all mOutputStream settings to default
     *
     *//*
*/
/*

    public void resetToDefault(){
        setInverse(false);
        setBold(false);
        setUnderline(0);
        setJustification(0);
    }

*/
/**
     *
     * @param txt
     *          String to print
     *//*


    public void printString(String str){
        //escInit();
        mOutputStream.write(str);
        mOutputStream.write(0xA);
    }

    public void storeString(String str){
        mOutputStream.write(str);
    }

    public void storeChar(int hex){
        mOutputStream.write(hex);
    }

    public void printStorage(){
        mOutputStream.write(0xA);
    }

*/
/**
     * Prints n lines of blank paper.
     * *//*


    public void feed(int feed){
        //escInit();
        mOutputStream.write(0x1B);
        mOutputStream.write("d");
        mOutputStream.write(feed);
    }


*/
/**
     * Prints a string and outputs n lines of blank paper.
     * *//*



    public void printAndFeed(String str, int feed){
        //escInit();
        mOutputStream.write(str);
        //output extra paper
        mOutputStream.write(0x1B);
        mOutputStream.write("d");
        mOutputStream.write(feed);
    }


*/
/**
     * Sets bold
     * *//*


    public void setBold(Boolean bool){
        mOutputStream.write(0x1B);
        mOutputStream.write("E");
        mOutputStream.write((int)(bool?1:0));
    }


*/
/**
     * Sets white on black printing
     * *//*


    public void setInverse(Boolean bool){
        mOutputStream.write(0x1D);
        mOutputStream.write("B");
        mOutputStream.write( (int)(bool?1:0) );
    }


*/
/**
     * Sets underline and weight
     *
     * @param val
     * 		0 = no underline.
     * 		1 = single weight underline.
     * 		2 = double weight underline.
     * *//*



    public void setUnderline(int val){
        mOutputStream.write(0x1B);
        mOutputStream.write("-");
        mOutputStream.write(val);
    }



*/
/**
     * Sets left, center, right justification
     *
     * @param val
     * 		0 = left justify.
     * 		1 = center justify.
     * 		2 = right justify.
     * *//*



    public void setJustification(int val){
        mOutputStream.write(0x1B);
        mOutputStream.write("a");
        mOutputStream.write(val);
    }


*/
/**
     * Encode and print QR code
     *
     * @param str
     *          String to be encoded in QR.
     * @param errCorrection
     *          The degree of error correction. (48 <= n <= 51)
     *          48 = level L / 7% recovery capacity.
     *          49 = level M / 15% recovery capacity.
     *          50 = level Q / 25% recovery capacity.
     *          51 = level H / 30% recovery capacity.
     *
     *  @param moduleSize
     *  		The size of the QR module (pixel) in dots.
     *  		The QR code will not print if it is too big.
     *  		Try setting this low and experiment in making it larger.
     *//*


    public void printQR(String str, int errCorrect, int moduleSize){
        //save data function 80
        mOutputStream.write(0x1D);//init
        mOutputStream.write("(k");//adjust height of barcode
        mOutputStream.write(str.length()+3); //pl
        mOutputStream.write(0); //ph
        mOutputStream.write(49); //cn
        mOutputStream.write(80); //fn
        mOutputStream.write(48); //
        mOutputStream.write(str);

        //error correction function 69
        mOutputStream.write(0x1D);
        mOutputStream.write("(k");
        mOutputStream.write(3); //pl
        mOutputStream.write(0); //ph
        mOutputStream.write(49); //cn
        mOutputStream.write(69); //fn
        mOutputStream.write(errCorrect); //48<= n <= 51

        //size function 67
        mOutputStream.write(0x1D);
        mOutputStream.write("(k");
        mOutputStream.write(3);
        mOutputStream.write(0);
        mOutputStream.write(49);
        mOutputStream.write(67);
        mOutputStream.write(moduleSize);//1<= n <= 16

        //print function 81
        mOutputStream.write(0x1D);
        mOutputStream.write("(k");
        mOutputStream.write(3); //pl
        mOutputStream.write(0); //ph
        mOutputStream.write(49); //cn
        mOutputStream.write(81); //fn
        mOutputStream.write(48); //m
    }


*/
/**
     * Encode and print barcode
     *
     * @param code
     *          String to be encoded in the barcode.
     *          Different barcodes have different requirements on the length
     *          of data that can be encoded.
     * @param type
     *          Specify the type of barcode
     *          65 = UPC-A.
     *          66 = UPC-E.
     *          67 = JAN13(EAN).
     *          68 = JAN8(EAN).
     *          69 = CODE39.
     *          70 = ITF.
     *          71 = CODABAR.
     *          72 = CODE93.
     *          73 = CODE128.
     *
     *  @param h
     *  		height of the barcode in points (1 <= n <= 255)
     *  @param w
     *  		width of module (2 <= n <=6).
     *  		Barcode will not print if this value is too large.
     *  @param font
     *  		Set font of HRI characters
     *  		0 = font A
     *  		1 = font B
     *  @param pos
     *  		set position of HRI characters
     *  		0 = not printed.
     *  		1 = Above barcode.
     *  		2 = Below barcode.
     *  		3 = Both above and below barcode.
     *//*


    public void printBarcode(String code, int type, int h, int w, int font, int pos){

        //need to test for errors in length of code
        //also control for input type=0-6

        //GS H = HRI position
        mOutputStream.write(0x1D);
        mOutputStream.write("H");
        mOutputStream.write(pos); //0=no print, 1=above, 2=below, 3=above & below

        //GS f = set barcode characters
        mOutputStream.write(0x1D);
        mOutputStream.write("f");
        mOutputStream.write(font);

        //GS h = sets barcode height
        mOutputStream.write(0x1D);
        mOutputStream.write("h");
        mOutputStream.write(h);

        //GS w = sets barcode width
        mOutputStream.write(0x1D);
        mOutputStream.write("w");
        mOutputStream.write(w);//module = 1-6

        //GS k
        mOutputStream.write(0x1D); //GS
        mOutputStream.write("k"); //k
        mOutputStream.write(type);//m = barcode type 0-6
        mOutputStream.write(code.length()); //length of encoded string
        mOutputStream.write(code);//d1-dk
        mOutputStream.write(0);//print barcode
    }


*/
/**
     * Encode and print PDF 417 barcode
     *
     * @param code
     *          String to be encoded in the barcode.
     *          Different barcodes have different requirements on the length
     *          of data that can be encoded.
     * @param type
     *          Specify the type of barcode
     *          0 - Standard PDF417
     *          1 - Standard PDF417
     *
     *  @param h
     *  		Height of the vertical module in dots 2 <= n <= 8.
     *  @param w
     *  		Height of the horizontal module in dots 1 <= n <= 4.
     *  @param cols
     *  		Number of columns 0 <= n <= 30.
     *  @param rows
     *  		Number of rows 0 (automatic), 3 <= n <= 90.
     *  @param error
     *  		set error correction level 48 <= n <= 56 (0 - 8).
     *
     *//*


    public void printPSDCode(String code, int type, int h, int w, int cols, int rows, int error){

        //print function 82
        mOutputStream.write(0x1D);
        mOutputStream.write("(k");
        mOutputStream.write(code.length()); //pl Code length
        mOutputStream.write(0); //ph
        mOutputStream.write(48); //cn
        mOutputStream.write(80); //fn
        mOutputStream.write(48); //m
        mOutputStream.write(code); //data to be encoded


        //function 65 specifies the number of columns
        mOutputStream.write(0x1D);//init
        mOutputStream.write("(k");//adjust height of barcode
        mOutputStream.write(3); //pl
        mOutputStream.write(0); //pH
        mOutputStream.write(48); //cn
        mOutputStream.write(65); //fn
        mOutputStream.write(cols);

        //function 66 number of rows
        mOutputStream.write(0x1D);//init
        mOutputStream.write("(k");//adjust height of barcode
        mOutputStream.write(3); //pl
        mOutputStream.write(0); //pH
        mOutputStream.write(48); //cn
        mOutputStream.write(66); //fn
        mOutputStream.write(rows); //num rows

        //module width function 67
        mOutputStream.write(0x1D);
        mOutputStream.write("(k");
        mOutputStream.write(3);//pL
        mOutputStream.write(0);//pH
        mOutputStream.write(48);//cn
        mOutputStream.write(67);//fn
        mOutputStream.write(w);//size of module 1<= n <= 4

        //module height fx 68
        mOutputStream.write(0x1D);
        mOutputStream.write("(k");
        mOutputStream.write(3);//pL
        mOutputStream.write(0);//pH
        mOutputStream.write(48);//cn
        mOutputStream.write(68);//fn
        mOutputStream.write(h);//size of module 2 <= n <= 8

        //error correction function 69
        mOutputStream.write(0x1D);
        mOutputStream.write("(k");
        mOutputStream.write(4);//pL
        mOutputStream.write(0);//pH
        mOutputStream.write(48);//cn
        mOutputStream.write(69);//fn
        mOutputStream.write(48);//m
        mOutputStream.write(error);//error correction

        //choose pdf417 type function 70
        mOutputStream.write(0x1D);
        mOutputStream.write("(k");
        mOutputStream.write(3);//pL
        mOutputStream.write(0);//pH
        mOutputStream.write(48);//cn
        mOutputStream.write(70);//fn
        mOutputStream.write(type);//set mode of pdf 0 or 1

        //print function 81
        mOutputStream.write(0x1D);
        mOutputStream.write("(k");
        mOutputStream.write(3); //pl
        mOutputStream.write(0); //ph
        mOutputStream.write(48); //cn
        mOutputStream.write(81); //fn
        mOutputStream.write(48); //m

    }



*/
/**
     * Store custom character
     * input array of column bytes
     * @param columnArray
     * 		Array of bytes (0-255). Ideally not longer than 24 bytes.
     *
     * @param mode
     * 		0 - 8-dot single-density.
     * 		1 - 8-dot double-density.
     * 		32 - 24-dot single density.
     * 		33 - 24-dot double density.
     *//*


    public void storeCustomChar(int[] columnArray, int mode){

        //function GS*
        mOutputStream.write(0x1B);
        mOutputStream.write("*");
        mOutputStream.write(mode);
        mOutputStream.write( (mode==0 ||mode==1)? columnArray.length : columnArray.length / 3 );//number of cols
        mOutputStream.write(0);
        for (int i = 0 ; i < columnArray.length ; i++ )
        {
            mOutputStream.write(columnArray[i]);
        }

    }


*/
/**
     * Store custom character
     * input array of column bytes.	NOT WORKING
     * @param spacing
     * 		Integer representing Vertical motion of unit in inches. 0-255
     *
     *//*


    public void setLineSpacing(int spacing){

        //function ESC 3
        mOutputStream.write(0x1B);
        mOutputStream.write("3");
        mOutputStream.write(spacing);

    }

    public void cut(){
        mOutputStream.write(0x1D);
        mOutputStream.write("V");
        mOutputStream.write(48);
        mOutputStream.write(0);
    }

    public void feedAndCut(int feed){

        feed(feed);
        cut();
    }

    public void beep()
    {
        mOutputStream.write(0x1B);
        mOutputStream.write("(A");
        mOutputStream.write(4);
        mOutputStream.write(0);
        mOutputStream.write(48);
        mOutputStream.write(55);
        mOutputStream.write(3);
        mOutputStream.write(15);
    }



*/
/**
     *
     * Print a sample sheet
     *
     *//*


    public void printSampler(){
        //print samples of all functions here
        resetToDefault();
        escInit();
        storeChar(178);
        storeChar(177);
        storeChar(176);

        storeString("Hello World");
        printStorage();


        printString("printString();");
        setBold(true);
        printString("setBold(true)");
        setBold(false);
        setUnderline(1);
        printString("setUnderline(1)");
        setUnderline(2);
        printString("setUnderline(2)");
        setUnderline(0);
        setInverse(true);
        printString("setInverse(true)");
        setInverse(false);
        setJustification(0);
        printString("setJustification(0)\n//left - default");
        setJustification(1);
        printString("setJustification(1)\n//center");
        setJustification(2);
        printString("setJustification(2)\n//right");
        setJustification(1);
        printQR("http://www.josephbergen.com", 51, 8);
        printAndFeed("\n##name## ##version##\nby Joseph Bergen\nwww.josephbergen.com", 4);
        resetToDefault();
    }
}*/
