$(document).ready(function() {
    // year prefix
    $("#yearPrefix").text(plural($("#doctorExperience").text()));

    // Doctor specialities
    if (doctorSpecialities.length === 0) {
        console.log("doctorSpecialities.length is 0");
    }
    else {
        let specialitiesColumns = "";
        for (let i = 0; i < doctorSpecialities.length; i++) {
            if (i === 3) break;
            specialitiesColumns += '<div class="col col-md-4 p-0">\n' +
                '                                                <button class="button tag">' + doctorSpecialities[i] + '</button>\n' +
                '                                            </div>';
        }
        $("#doctorSpecialities").html(specialitiesColumns);
    }

    // ResumeParts render
    const edjsParser = edjsHTML();
    console.log(aboutDoctor);
    console.log(education);
    console.log(workPlaces);
    $("#aboutDoctorDiv").html(edjsParser.parse(JSON.parse(aboutDoctor)));
    $("#educationDiv").html(edjsParser.parse(JSON.parse(education)));
    $("#workPlacesDiv").html(edjsParser.parse(JSON.parse(workPlaces)));

    console.log(html);
});