import HeaderNav from '@/components/core/header/header-nav';
import Burger from '@/components/custom/burger/burger';
import CloseTrigger from '@/components/custom/close-trigger/close-trigger';
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

  if (currentBreakpoint !== 'sm') {
    return;
  }

  return (
    <DrawerRoot open={open} closeOnEscape closeOnInteractOutside>
      <DrawerBackdrop />
      <DrawerTrigger asChild>
        <Burger
          onClick={() => onOpen()}
          fill={currentTheme === 'light' ? 'black' : 'white'}
        />
      </DrawerTrigger>
      <DrawerContent
        minH="100vh"
        position="fixed"
        top={0}
        right={0}
        bg="surface"
      >
        <DrawerHeader py="7">
          <CloseTrigger onClick={() => onClose()} />
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
