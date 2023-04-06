export let darkOptions = {
  dark: true,
  responsive: true,
  plugins: {
    responsive: true,
    legend: {
      title: {
        display: false,
      },
      display: false,
    },
  },
  scales: {
    x: {
      ticks: {
        maxRotation: 90,
        minRotation: 15,
        color: "white",
      },
      grid: {
          color: "white",
        },
    },
    y: {
      ticks: {
        color: "white",
      },
      grid: {
        color: "white",
      },
    },
  },
};

export let lightOptions = {
  light: true,
  responsive: true,
  plugins: {
    responsive: true,
    legend: {
      title: {
        display: false,
      },
      display: false,
    },
  },
  scales: {
    x: {
      ticks: {
        maxRotation: 90,
        minRotation: 15,
        color: "#4b5563",
      },
      grid: {
          color: "#4b5563",
        },
    },
    y: {
      ticks: {
        color: "#4b5563",
      },
      grid: {
        color: "#4b5563",
      },
    },
  },
};