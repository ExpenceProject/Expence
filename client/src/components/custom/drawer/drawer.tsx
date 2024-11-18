import HeaderNav from '@/components/core/header/header-nav';
import Burger from '@/components/icons/burger/burger';
import CloseTrigger from '@/components/icons/close-trigger/close-trigger';
import { useColorModeValue } from '@/components/ui/color-mode';
import {
  DrawerBackdrop,
  DrawerBody,
  DrawerCloseTrigger,
  DrawerContent,
  DrawerHeader,
  DrawerRoot,
  DrawerTrigger,
  useBreakpointValue,
  useDisclosure,
} from '@chakra-ui/react';

const Drawer = () => {
  const { open, onOpen, onClose } = useDisclosure();

  const currentTheme = useColorModeValue('light', 'dark');

  const currentBreakpoint = useBreakpointValue({
    sm: 'sm',
    md: 'md',
    lg: 'lg',
    xl: 'xl',
  });

  if (currentBreakpoint && currentBreakpoint !== 'sm') {
    return;
  }

  return (
    <DrawerRoot open={open} placement="end" onEscapeKeyDown={onClose}>
      <DrawerBackdrop
        bg="rgba(0, 0, 0, 0.4)"
        _dark={{ bg: 'rgba(0, 0, 0, 0.7 )' }}
        css={{ backdropFilter: 'blur(2px)' }}
        // These two props below are necessary for the backdrop to close the drawer, it's probably a bug in Chakra UI
        onClick={onClose}
        pointerEvents="auto"
      />
      <DrawerTrigger>
        <Burger
          onClick={onOpen}
          fill={currentTheme === 'light' ? 'black' : 'white'}
        />
      </DrawerTrigger>
      <DrawerContent h="100vh" position="fixed" top={0} right={0} bg="surface">
        <DrawerHeader py="7">
          <CloseTrigger onClick={onClose} />
        </DrawerHeader>
        <DrawerBody>
          <HeaderNav direction="column" />
        </DrawerBody>
        <DrawerCloseTrigger />
      </DrawerContent>
    </DrawerRoot>
  );
};

export default Drawer;
