$(document).ready(function() {
    const aboutDoctorEditor = new EditorJS({
        holder: 'inputAboutDoctor',
        autofocus: true,
        tools: {
            list: {
                class: List,
                inlineToolbar: true,
                config: {
                    defaultStyle: 'ordered'
                }
            }
        }
    });

    const educationEditor = new EditorJS({
        holder: 'inputEducation',
        autofocus: true,
        tools: {
            list: {
                class: List,
                inlineToolbar: true,
                config: {
                    defaultStyle: 'ordered'
                }
            }
        }
    });

    const workPlacesEditor = new EditorJS({
        holder: 'inputWorkPlaces',
        autofocus: true,
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

    $("#regSubmit").on('click', function() {
        aboutDoctorEditor.save().then((outputData) => {
            $("#inputAboutDoctorResult").val(JSON.stringify(outputData));
        }).catch((error) => {
            console.log('Saving failed: ', error)
        });
        educationEditor.save().then((outputData) => {
            $("#inputEducationResult").val(JSON.stringify(outputData));
        }).catch((error) => {
            console.log('Saving failed: ', error)
        });
        workPlacesEditor.save().then((outputData) => {
            $("#inputWorkPlacesResult").val(JSON.stringify(outputData));
        }).catch((error) => {
            console.log('Saving failed: ', error)
        });
    });
});