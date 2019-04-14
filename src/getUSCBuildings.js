const rp = require('request-promise');
const ch = require('cheerio');

const url = 'http://fmsmaps4.usc.edu/usc/php/bl_list_no.php';


rp(url)
	.then(function(html){
		var uscBuildingInfo = [];
		var arrayOfBuildings = [];
		ch('td', 'tr', html).each(function(i, elem) {
			uscBuildingInfo.push(ch(this).text());
		});
		for (var i=0; i<uscBuildingInfo.length; i += 4) {
			arrayOfBuildings.push({
				code: uscBuildingInfo[i+1].trim(),
				address: uscBuildingInfo[i+3].trim()
			});
		}
		console.log(arrayOfBuildings);
	})
	.catch(function(err){
		console.log(err);
	});