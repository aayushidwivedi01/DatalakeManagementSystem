/*! Bing Search Helper v1.0.0 - requires jQuery v1.7.2 */


// Shows one item of Web result.
function showWebResult(item)
{
var p = document.createElement('p');
var a = document.createElement('a');
a.href = item.Url;
$(a).append(item.Title);
$(p).append(item.Description);
// Append the anchor tag and paragraph with the description to the results div.
$('#results').append(a, p);
}

// Shows one item of Image result.

function showImageResult(item)
{
var p = document.createElement('p');
var a = document.createElement('a');
a.href = item.MediaUrl;
// Create an image element and set its source to the thumbnail.
var i = document.createElement('img');
i.src = item.Thumbnail.MediaUrl;
// Make the object that the user clicks the thumbnail image.
$(a).append(i);
$(p).append(item.Title);
// Append the anchor tag and paragraph with the title to the results div.
$('#images').append(a, p); 
} 