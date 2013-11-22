package com.jnctn.bulkupload.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;

/**
 * @author martin
 */
public class Phone implements IUploadable {

    public static String [] MAKES  = { "Polycom", "Panasonic", "Cisco", "Yealink", "Grandstream" };

    public static String [] MODELS = {
        "Polycom Soundpoint 301"   , "Polycom Soundpoint 320",
        "Polycom Soundpoint 321"   , "Polycom Soundpoint 330",
        "Polycom Soundpoint 331"   , "Polycom Soundpoint 335",
        "Polycom Soundpoint 430"   , "Polycom Soundpoint 450",
        "Polycom Soundpoint 501"   , "Polycom Soundpoint 550",
        "Polycom Soundpoint 560"   , "Polycom Soundpoint 601",
        "Polycom Soundpoint 650"   , "Polycom Soundpoint 670",
        "Polycom Soundstation 4000", "Polycom Soundstation 5000",
        "Polycom Soundstation 6000", "Polycom Soundstation 7000",
        "Polycom VVX 300"          , "Polycom VVX 310",
        "Polycom VVX 400"          , "Polycom VVX 410",
        "Polycom VVX 500"          , "Polycom VVX 600",
        "Polycom VVX 1500"         ,
        "Panasonic KX-UT113-B"     , "Panasonic KX-UT123-B",
        "Panasonic KX-UT133-B"     , "Panasonic KX-UT136-B",
        "Panasonic KX-UT670"       , "Panasonic KX-TGP500",
        "Panasonic KX-TGP550"      , "Cisco Linksys SPA901",
        "Cisco Linksys SPA921"     , "Cisco Linksys SPA922",
        "Cisco Linksys SPA941"     , "Cisco Linksys SPA942",
        "Cisco Linksys SPA962"     , "Cisco Linksys SPA2102",
        "Cisco Linksys SPA3102"    , "Cisco Linksys SPA8000",
        "Cisco Linksys SPA8800"    , "Cisco SPA112",
        "Cisco SPA303"             , "Cisco SPA501G",
        "Cisco SPA502G"            , "Cisco SPA504G",
        "Cisco SPA508G"            , "Cisco SPA509G",
        "Cisco SPA525G"            , "Cisco SPA525G2",
        "Yealink SIP-T20P"         , "Yealink SIP-T22P",
        "Yealink SIP-T26P"         , "Yealink SIP-T28P",
        "Yealink SIP-T32G"         , "Yealink SIP-T38G",
        "Yealink VP-2009P"         , "Grandstream GXP1405",
        "Grandstream GXP1450"      , "Grandstream GXP2124",
        "Grandstream GXP2100"      , "Grandstream GXP2110",
        "Grandstream GXP2120"      , "Grandstream GXP3140",
        "Grandstream GXP3175"      , "Grandstream HT503",
        "Grandstream HT502"
    };

    public static String [] GMT_OFFSET = {
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
	"-12"  , "-11.5", "-11"  , "-10.5", "-10"  ,
	"-9.5" , "-9"   , "-8.5" , "-8"   , "-7.5" ,
	"-7"   , "-6.5" , "-6"   , "-5.5" , "-5"   ,
	"-4.5" , "-4"   , "-3.5" , "-3"   , "-2.5" ,
	"-2"   , "-1.5" , "-1"   , "-.5"  , "0"    ,
	"13"   , "12.5" , "12"   , "11.5" , "11"   ,
	"10.5" , "10"   , "9.5"  , "9"    , "8.5"  ,
	"8"    , "7.5"  , "7"    , "6.5"  , "6"    ,
	"5.5"  , "5"    , "4.5"  , "4"    , "3.5"  ,
	"3"    , "2.5"  , "2"    , "1.5"  , "1"    ,
	".5"
    };

    private String _make;
    private String _error;
    private String _model;
    private String _gmtOffset;
    private String _macAddress;
    private boolean _hasNatKeepalive;
    private boolean _hasOrganizationWebPassword;

    public String getError() {
	return _error;
    }

    public void setMacAddress(String macAddress) {
	this._macAddress = macAddress;
    }

    public void setMake(String make) {
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

    public void setNatKeepalive(boolean hasNatkeepalive) {
        this._hasNatKeepalive = hasNatkeepalive;
    }

    public void setOrganizationWebPassword(boolean hasOrganizationWebPassword) {
        this._hasOrganizationWebPassword = hasOrganizationWebPassword;
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

    public boolean getOrganizationWebPassword() {
        return this._hasOrganizationWebPassword;
    }

    public boolean getNatKeepalive() {
        return this._hasNatKeepalive;
    }

    @Override
    public String toString() {
	return "Phone{" +
	    "error=" + (StringUtils.isEmpty(this._error) ? "NONE" : this._error) +
	    ";macaddress=" + this._macAddress +
	    ";make=" + this._make       +
	    ";model=" + this._model      +
            ";natkeepalive=" + this._hasNatKeepalive +
            ";organizationwebpassword=" + this._hasOrganizationWebPassword +
	    ";gmtOffset="  + this._gmtOffset  + "}";
    }
}
