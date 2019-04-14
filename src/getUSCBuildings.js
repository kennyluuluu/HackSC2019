const rp = require('request-promise');
const ch = require('cheerio');

const url = 'http://fmsmaps4.usc.edu/usc/php/bl_list_no.php';

var uscBuildingInfo;

rp(url)
	.then(function(html){
		var info = ch('td', 'tr', html).text();
		console.log(uscBuildingInfo);
	})
	.catch(function(err){
		console.log(err);
	})