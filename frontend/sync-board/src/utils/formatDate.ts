const locale = "en-US";

const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
};

export const today = () => {
    return new Date().toLocaleDateString(locale, options)
}

export const formatDate = (date: Date) => {
    return date.toLocaleDateString(locale, options);
}

export const formatDateStr = (date: string) => {
    return formatDate(new Date(date));
}

