package com.jctn.bulkupload.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;

/**
 * @author martin
 */
public class Phone {

    public static String [] MAKES      = { "Polycom" };

    public static String [] MODELS             = {
	"Polycom Soundpoint 301"   , "Polycom Soundpoint 320",
	"Polycom Soundpoint 321"   , "Polycom Soundpoint 330",
	"Polycom Soundpoint 331"   , "Polycom Soundpoint 335",
	"Polycom Soundpoint 430"   , "Polycom Soundpoint 450",
	"Polycom Soundpoint 501"   , "Polycom Soundpoint 550",
	"Polycom Soundpoint 560"   , "Polycom Soundpoint 601",
	"Polycom Soundpoint 650"   , "Polycom Soundpoint 670",
	"Polycom Soundstation 4000", "Polycom Soundstation 6000",
	"Polycom Soundstation 7000"
    };

    public static String [] GMT_OFFSET        = {

	"-43200" , "-41400", "-39600", "-37800", "-36000",
	"-34200" , "-32400", "-30600", "-28800", "-27000",
	"-25200" , "-23400", "-21600", "-19800", "-18000",
	"-16200" , "-14400", "-12600", "-10800", "-9000" ,
	"-7200"  , "-5400" , "-3600" , "-1800" , "0"     ,
	"46800"  , "45000" , "43200" , "41400" , "39600" , 
	"37800"  , "36000" , "34200" , "32400" , "30600" , 
	"28800"  , "27000" , "25200" , "23400" , "21600" , 
	"19800"  , "18000" ,"16200"  , "14400" , "12600" , 
	"10800"  , "9000"  ,"7200"   , "5400"  , "3600"  , 
	"1800"  
    };

    public static String [] GMT_OFFSET_ALIAS = {
	"-12"  , "-11.5", "-11"  , "-10.5", "-10" ,
	"-9.5" , "-9"   , "-8.5" , "-8"   , "-7.5" ,
	"-7"   , "-6.5" , "-6"   , "-5.5" , "-5"   ,
	"-4.5" , "-4"   , "-3.5" , "-3"   , "-2.5" ,
	"-2"   , "-1.5" , "-1"   , "-.5"  , "0"    ,
	"13"   , "12.5" , "12"   , "11.5" , "11"   , 
	"10.5" , "10"   , "9.5"  , "9"    , "8.5"  , 
	"8"    , "7.5"  , "7"    , "6.5"  , "6"    , 
	"5.5" , "5"   , "4.5"  , "4"    , "3.5"  , 
	"3"   , "2.5" , "2"    , "1.5"  , "1"    , 
	".5" 
    };

    
    private String _macAddress;
    private String _make;
    private String _model;
    private String _gmtOffset;
    private String _error;

    public String getError() {
	return _error;
    }

    public void setMacAddress (String macAddress) {
	this._macAddress = macAddress;
    }

    public void setMake (String make) {
	this._make = make;
    }

    public void setError(String errorString) {
	this._error = errorString;
    }

    public void setModel (String model) {
	this._model = model;
    }

    public void setGmtOffset (String gmtOffset) {
	this._gmtOffset = gmtOffset;
    }
    
    public String getMacAddress () {
	return this._macAddress;
    }

    public String getMake () {
	return this._make;
    }
    
    public String getModel () {
	return this._model;
    }

    public String getGmtOffset () {
	return this._gmtOffset;
    }

    @Override
    public String toString() {
	return "Phone{" + 
	    "error="       + (StringUtils.isEmpty(this._error) ? "NONE" : this._error) + 
	    ";macaddress=" + this._macAddress + 
	    ";make="       + this._make       + 
	    ";model="      + this._model      + 
	    ";gmtOffset="  + this._gmtOffset  + "}";
    }
}
