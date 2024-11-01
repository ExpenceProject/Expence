import { createSystem, defaultConfig, defineConfig } from '@chakra-ui/react';

const semanticTokens = {
  colors: {
    primary: {
      value: {
        _light: '#3a9e7c',
        _dark: '#3a9e7c',
      },
    },
    textRaw: {
      value: {
        _light: '#000000',
        _dark: '#ffffff',
      },
    },
    text: {
      value: {
        _light: '#656565',
        _dark: '#dedede',
      },
    },
    textBg: {
      value: {
        _light: '#ffffff',
        _dark: '#ededed',
      },
    },
    textHover: {
      value: {
        _light: '#000000',
        _dark: '#ffffff',
      },
    },
    background: {
      value: {
        _light: '#f7f4ef',
        _dark: '#1a1a1a',
      },
    },
    surface: {
      value: {
        _light: '#ffffff',
        _dark: '#2f2f2f',
      },
    },
    border: {
      value: {
        _light: '#dcdcdc',
        _dark: '#4a4a4a',
      },
    },
    hover: {
      value: {
        _light: '#4fa488',
        _dark: '#60b497',
      },
    },
    disabled: {
      value: {
        _light: '#a0a0a0',
        _dark: '#4a4a4a',
      },
    },
    highlight: {
      value: {
        _light: '#3a9e7c',
        _dark: '#88c8b1',
      },
    },
  },
};

const breakpoints = {
  sm: '320px',
  md: '768px',
  lg: '960px',
  xl: '1200px',
};

const globalCss = {
  '*': {
    boxSizing: 'border-box',
  },
  'html, body': {
    margin: 0,
    padding: 0,
    minHeight: '100vh',
    width: '100vw',
    color: 'var(--ck-colors-text)',
    backgroundColor: 'var(--ck-colors-background)',
  },
};

const config = defineConfig({
  theme: {
    semanticTokens,
    breakpoints,
  },
  strictTokens: true,
  cssVarsPrefix: 'ck',
  globalCss,
});

// Create the system with default and custom configuration
export const theme = createSystem(defaultConfig, config);
