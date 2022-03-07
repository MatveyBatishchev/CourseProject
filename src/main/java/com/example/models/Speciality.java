package com.example.models;

public enum Speciality {
    Allergists("Аллерголог"),
    Andrologist("Андролог"),
    Anesthetist("Анастезиолог"),
    Arrhythmologist("Аритмолог"),
    Arthrologist("Артролог"),

    Venerologist("Венеролог"),

    Gastroenterologist("Гастроэнтеролог"),
    Hematologist("Гематолог"),
    Hemostasiologist("Гемостазиолог"),
    Geneticist("Генетик"),
    Hepatologist("Гепатолог"),
    Gynecologist("Гинеколог"),
    Hirudotherapist("Гирудотерапевт"),

    Dermatologist("Дерматолог"),
    Nutritionist("Диетолог"),

    Immunologist("Иммунолог"),
    Infectionist("Инфекционист"),

    Cardiologist("Кардиолог"),
    Cosmetologist("Косметолог"),
    CT_diagnostician("КТ-диагност"),

    Speech_therapist_aphasiologist("Логопед-афазиолог"),

    Mammologist("Маммолог"),
    Chiropractor("Мануальный терапевт"),
    Masseur("Массажист"),
    Mycologist("Миколог"),
    MRI_diagnostician("МРТ-диагност"),

    Neurologist("Невролог"),
    Neurosurgeon("Нейрохирург"),
    Nephrologist("Нефролог"),

    Oncologist("Онколог"),
    Osteopath("Остеопат"),
    Otolaryngologist("Отоларинголог"),
    Ophthalmologist("Офтальмолог"),

    Pediatrician("Педиатр"),
    Podiatrist("Подолог"),
    Proctologist("Проктолог"),
    Psychiatrist("Психиатр"),
    Psychologist("Психолог"),
    Psychotherapist("Психотерапевт"),
    Pulmonologist("Пульмонолог"),

    Rehabilitologist("Реабилитолог"),
    Rheumatologist("Ревматолог"),
    Radiologist("Рентгенолог"),
    Reproductologist("Репродуктолог"),
    Reflexologist(" Рефлексотерапевт"),

    Sexologist("Сексолог"),
    Cardiovascular_surgeon("Сердечно-сосудистый хирург"),
    Somnologist("Сомнолог"),
    Dentist("Стоматолог"),

    Therapist("Терапевт"),
    Traumatologist_orthopedist("Травматолог-ортопед"),
    Transfusiologist("Трансфузиолог"),
    Trichologist("Трихолог"),

    Ultrasound_diagnostician("УЗ-диагност"),
    Urologist("Уролог"),

    Physiotherapist("Физеотерапевт"),
    Phlebologist("Флеболог"),
    Foniatr("Фониатр"),
    Functional_diagnostician("Функциональный диагност"),

    Surgeon("Хирург"),

    Embryologist("Эмбриолог"),
    Endocrinologist("Эндокринолог"),
    Endoscopist("Эндоскопист"),
    Epileptologist("Эпилептолог");

    public final String label;

    Speciality(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
