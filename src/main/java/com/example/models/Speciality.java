package com.example.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Speciality {
    @SerializedName("Аллерголог") Allergists("Аллерголог"),
    @SerializedName("Андролог") Andrologist("Андролог"),
    @SerializedName("Анастезиолог") Anesthetist("Анастезиолог"),
    @SerializedName("Аритмолог") Arrhythmologist("Аритмолог"),
    @SerializedName("Артролог") Arthrologist("Артролог"),

    @SerializedName("Венеролог") Venerologist("Венеролог"),

    @SerializedName("Гастроэнтеролог") Gastroenterologist("Гастроэнтеролог"),
    @SerializedName("Гематолог") Hematologist("Гематолог"),
    @SerializedName("Гемостазиолог") Hemostasiologist("Гемостазиолог"),
    @SerializedName("Генетик") Geneticist("Генетик"),
    @SerializedName("Гепатолог") Hepatologist("Гепатолог"),
    @SerializedName("Гинеколог") Gynecologist("Гинеколог"),
    @SerializedName("Гирудотерапевт") Hirudotherapist("Гирудотерапевт"),

    @SerializedName("Дерматолог") Dermatologist("Дерматолог"),
    @SerializedName("Диетолог") Nutritionist("Диетолог"),

    @SerializedName("Иммунолог") Immunologist("Иммунолог"),
    @SerializedName("Инфекционист") Infectionist("Инфекционист"),

    @SerializedName("Кардиолог") Cardiologist("Кардиолог"),
    @SerializedName("Косметолог") Cosmetologist("Косметолог"),
    @SerializedName("КТ-диагност") CT_diagnostician("КТ-диагност"),

    @SerializedName("Логопед") Speech_therapist("Логопед"),

    @SerializedName("Маммолог") Mammologist("Маммолог"),
    @SerializedName("Мануальный терапевт") Chiropractor("Мануальный терапевт"),
    @SerializedName("Массажист") Masseur("Массажист"),
    @SerializedName("Миколог") Mycologist("Миколог"),
    @SerializedName("МРТ-диагност") MRI_diagnostician("МРТ-диагност"),

    @SerializedName("Невролог") Neurologist("Невролог"),
    @SerializedName("Нейрохирург") Neurosurgeon("Нейрохирург"),
    @SerializedName("Нефролог") Nephrologist("Нефролог"),

    @SerializedName("Онколог") Oncologist("Онколог"),
    @SerializedName("Остеопат") Osteopath("Остеопат"),
    @SerializedName("Отоларинголог") Otolaryngologist("Отоларинголог"),
    @SerializedName("Офтальмолог") Ophthalmologist("Офтальмолог"),

    @SerializedName("Педиатр") Pediatrician("Педиатр"),
    @SerializedName("Подолог") Podiatrist("Подолог"),
    @SerializedName("Проктолог") Proctologist("Проктолог"),
    @SerializedName("Психиатр") Psychiatrist("Психиатр"),
    @SerializedName("Психолог") Psychologist("Психолог"),
    @SerializedName("Психотерапевт") Psychotherapist("Психотерапевт"),
    @SerializedName("Пульмонолог") Pulmonologist("Пульмонолог"),

    @SerializedName("Реабилитолог") Rehabilitologist("Реабилитолог"),
    @SerializedName("Ревматолог") Rheumatologist("Ревматолог"),
    @SerializedName("Рентгенолог") Radiologist("Рентгенолог"),
    @SerializedName("Репродуктолог") Reproductologist("Репродуктолог"),
    @SerializedName("Рефлексотерапевт") Reflexologist("Рефлексотерапевт"),

    @SerializedName("Сексолог") Sexologist("Сексолог"),
    @SerializedName("Сердечно-сосудистый хирург") Cardiovascular_surgeon("Сердечно-сосудистый хирург"),
    @SerializedName("Сомнолог") Somnologist("Сомнолог"),
    @SerializedName("Стоматолог") Dentist("Стоматолог"),

    @SerializedName("Терапевт") Therapist("Терапевт"),
    @SerializedName("Травматолог-терапевт") Traumatologist_orthopedist("Травматолог-ортопед"),
    @SerializedName("Трансфузиолог") Transfusiologist("Трансфузиолог"),
    @SerializedName("Трихолог") Trichologist("Трихолог"),

    @SerializedName("УЗ-диагност") Ultrasound_diagnostician("УЗ-диагност"),
    @SerializedName("Уролог") Urologist("Уролог"),

    @SerializedName("Физеотерапевт") Physiotherapist("Физеотерапевт"),
    @SerializedName("Флеболог") Phlebologist("Флеболог"),
    @SerializedName("Фониатр") Foniatr("Фониатр"),
    @SerializedName("Функциональный диагност") Functional_diagnostician("Функциональный диагност"),

    @SerializedName("Хирург") Surgeon("Хирург"),

    @SerializedName("Эмбриолог") Embryologist("Эмбриолог"),
    @SerializedName("Эндокринолог") Endocrinologist("Эндокринолог"),
    @SerializedName("Эндоскопист") Endoscopist("Эндоскопист"),
    @SerializedName("Эпилептолог") Epileptologist("Эпилептолог");

    public final String label;

    @Override
    public String toString() {
        return label;
    }
}
