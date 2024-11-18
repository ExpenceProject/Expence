import { Button, Link } from '@chakra-ui/react';
import { FC } from 'react';

type ButtonLinkProps = {
  text: string;
  href: string;
  backgroundColor?: string;
  color?: string;
  hoverColor?: string;
  fontSize?: string;
  px?: number;
  py?: number;
};

const ButtonLink: FC<ButtonLinkProps> = ({
  text,
  href,
  backgroundColor = 'primary',
  color = 'textBg',
  hoverColor = 'hover',
  fontSize = 'lg',
  px = 4,
  py = 0,
}) => {
  return (
    <Button
      fontSize={fontSize}
      bg={backgroundColor}
      _hover={{ bg: hoverColor }}
      p={0}
    >
      <Link
        color={color}
        href={href}
        textDecor="none"
        w="100%"
        h="100%"
        _focus={{ outline: 'none' }}
        px={px}
        py={py}
      >
        {text}
      </Link>
    </Button>
  );
};

export default ButtonLink;
