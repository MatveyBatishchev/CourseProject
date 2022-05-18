$(document).ready(function() {
    const aboutDoctorEditor = new EditorJS({
        holder: 'inputAboutDoctor',
        autofocus: true,
        readOnly: true,
        data: JSON.parse(aboutDoctor),
        tools: {
            list: {
                class: List,
                inlineToolbar: true,
                config: {
                    defaultStyle: 'unordered'
                }
            }
        }
    });

    const educationEditor = new EditorJS({
        holder: 'inputEducation',
        autofocus: true,
        data: JSON.parse(education),
        readOnly: true,
        tools: {
            list: {
                class: List,
                inlineToolbar: true,
                config: {
                    defaultStyle: 'unordered'
                }
            }
        }
    });

    const workPlacesEditor = new EditorJS({
        holder: 'inputWorkPlaces',
        autofocus: true,
        data: JSON.parse(workPlaces),
        readOnly: true,
        tools: {
            list: {
                class: List,
                inlineToolbar: true,
                config: {
                    defaultStyle: 'unordered'
                }
            }
        }
    });

    let editResumeButton = $("#editResumeButton");
    editResumeButton.on('click', function () {
        if ($(this).text() === 'Редактировать') {
            aboutDoctorEditor.readOnly.toggle();
            workPlacesEditor.readOnly.toggle();
            educationEditor.readOnly.toggle();
            $(this).text('Подтвердить');
        } else {
            let aboutDoctorInput, educationInput, workPlacesInput;
            aboutDoctorEditor.save().then((outputData) => {
                aboutDoctorInput = JSON.stringify(outputData);
            }).catch((error) => {
                console.log('Saving failed: ', error)
            });
            educationEditor.save().then((outputData) => {
                 educationInput = JSON.stringify(outputData);
            }).catch((error) => {
                console.log('Saving failed: ', error)
            });
            workPlacesEditor.save().then((outputData) => {
                workPlacesInput = JSON.stringify(outputData);
            }).catch((error) => {
                console.log('Saving failed: ', error)
            });
            setTimeout(function() {
                $.ajax({
                    type: "PUT",
                    url: "/doctors/" + doctorId + "/resume/edit",
                    data: {
                        aboutDoctor: aboutDoctorInput,
                        education: educationInput,
                        workPlaces: workPlacesInput
                    },
                    success: function () {
                        aboutDoctorEditor.readOnly.toggle();
                        workPlacesEditor.readOnly.toggle();
                        educationEditor.readOnly.toggle();
                        editResumeButton.text('Редактировать');
                    },
                    error: function(error) {
                        console.log(error);
                    }
                });
            }, 10);
        }
    });

});